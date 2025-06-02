package com.example.web.service;

import com.example.web.dto.*;
import com.example.web.entity.*;
import com.example.web.repository.InverterAccumulationRepository;
import com.example.web.repository.InverterDataRepository;
import com.example.web.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InverterDataService {
    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;

    private final JunctionBoxDataService junctionBoxDataService;
    private final JunctionBoxDataAccumulationService junctionBoxDataAccumulationService;

    /**
     * 단상 인버터 데이터 저장
     *
     * @param inverterId 인버터 ID
     * @param dto        데이터 DTO
     */
    @Transactional
    public InverterData saveSinglePhaseData(Long inverterId, SinglePhaseInverterDto dto, LocalDateTime timestamp) {
        Inverter inverter = inverterRepository.findById(inverterId).orElseThrow(() -> new RuntimeException("Inverter not found"));
        SinglePhaseInverterData data = SinglePhaseInverterData.fromDTO(dto);
        data.setInverter(inverter);
        data.setTimestamp(timestamp);
        return inverterDataRepository.save(data);
    }

    /**
     * 삼상 인버터 데이터 저장
     *
     * @param inverterId 인버터 ID
     * @param dto        데이터 DTO
     */
    @Transactional
    public InverterData saveThreePhaseData(Long inverterId, ThreePhaseInverterDto dto, LocalDateTime timestamp) {
        Inverter inverter = inverterRepository.findById(inverterId).orElseThrow(() -> new RuntimeException("Inverter not found"));
        ThreePhaseInverterData data = ThreePhaseInverterData.fromDTO(dto, timestamp);
        data.setInverter(inverter);
        data.setTimestamp(timestamp);
        return inverterDataRepository.save(data);
    }

    /**
     * 모든 인버터의 최신 데이터 조회
     */
    public List<InvertersDataResponseDto> getAllInvertersLatestData() {
        List<InvertersDataResponseDto> responseList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        // 어제 마지막 시각 = 오늘 00:00 이전 1초
        LocalDateTime endOfYesterday = startOfToday.minusSeconds(1);
        LocalDateTime now = LocalDateTime.now();

        // 1) 모든 인버터 목록을 가져온 뒤, 각각에 대해 실시간 데이터와 오늘 누적을 계산
        List<Inverter> inverters = inverterRepository.findAll();
        for (Inverter inv : inverters) {
            Long inverterId = inv.getId();

            // 2) 오늘 최신 스냅샷 (00:00 이후 ~ 지금 사이)
            Optional<InverterData> latestTodayOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                            inverterId,
                            startOfToday,
                            now
                    );

            // 3) 어제 마지막 스냅샷 (< 오늘 00:00)
            Optional<InverterData> latestYesterdayOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inverterId,
                            endOfYesterday
                    );

            double todayGeneration;
            double realtimeKw = 0.0;
            LocalDateTime timestamp = null;

            if (latestTodayOpt.isPresent()) {
                InverterData todayData = latestTodayOpt.get();
                // (a) 오늘 누적 에너지
                double todayCum = todayData.getCumulativeEnergy();
                // (b) 어제 누적 에너지 (없으면 0)
                double prevCum = latestYesterdayOpt
                        .map(InverterData::getCumulativeEnergy)
                        .orElse(0.0);
                // (c) 오늘 발전량 = (오늘 누적) - (어제 누적)
                todayGeneration = todayCum - prevCum;

                // 실시간 전력량(kW)과 타임스탬프는 오늘 최신 스냅샷 기준
                realtimeKw = todayData.getPvPower() / 1000.0;
                timestamp = todayData.getTimestamp();
            } else {
                // 오늘 데이터가 아예 없으면, 발전량 0으로 간주
                todayGeneration = 0.0;
                // 실시간 전력량도 없으므로 그대로 0, timestamp는 null
            }

            // 4) DTO 생성 후 목록에 추가
            responseList.add(new InvertersDataResponseDto(
                    inverterId,
                    realtimeKw,
                    todayGeneration,
                    timestamp
            ));
        }

        return responseList;
    }

    /**
     * 인버터 상세 조회
     *
     * @param inverterId
     */
    @Transactional(readOnly = true)
    public InverterDetailResponseDto getInverterDetail(Long inverterId) {
        // 1) 인버터 존재 여부 확인
        Inverter inverter = inverterRepository.findById(inverterId)
                .orElseThrow(() -> new RuntimeException("인버터가 존재하지 않습니다. ID: " + inverterId));

        // 2) 최신 InverterData (실시간 스냅샷) 조회
        InverterData latestData = inverterDataRepository
                .findTopByInverterIdOrderByTimestampDesc(inverterId)
                .orElseThrow(() -> new RuntimeException("인버터의 데이터가 존재하지 않습니다. ID: " + inverterId));

        // 3) 오늘 시작 시각(00:00)과 어제 마지막 시각(23:59:59) 계산
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        // “어제 마지막” = “오늘 00:00” 바로 전 1초
        LocalDateTime endOfYesterday = startOfToday.minusSeconds(1);

        // 4) 오늘 마지막 누적 → latestData 에서 timestamp가 startOfToday 이후인 가장 최신 레코드
        //    (만약 오늘 사이클에 스냅샷이 여러 건 저장되었다면, findTopBy…BetweenOrderByTimestampDesc 사용)
        Optional<InverterData> latestTodayOpt = inverterDataRepository
                .findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                        inverterId,
                        startOfToday,
                        LocalDateTime.now()
                );

        // 5) 어제 마지막 누적 → endOfYesterday 이전(timestamp < startOfToday)의 가장 최신 레코드
        Optional<InverterData> latestYesterdayOpt = inverterDataRepository
                .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                        inverterId,
                        endOfYesterday
                );

        // 6) 오늘 발전량 계산 (diff)
        double todayGeneration;
        if (latestTodayOpt.isPresent()) {
            double todayCum = latestTodayOpt.get().getCumulativeEnergy();
            double prevCum = latestYesterdayOpt
                    .map(InverterData::getCumulativeEnergy)
                    .orElse(0.0);
            todayGeneration = todayCum - prevCum;
        } else {
            // 오늘 스냅샷이 없으면 발전량 0으로 간주
            todayGeneration = 0.0;
        }

        // 7) DTO 생성 및 반환 (여기에 cumulativeEnergy는 latestData.getCumulativeEnergy()로 전달하고,
        //    totalMonthlyCumulativeEnergy 대신 todayGeneration을 넘김)
        return InverterDetailResponseDto.fromInverterData(
                latestData,
                todayGeneration
        );
    }

    /**
     * 인버터 데이터 조회
     *
     * @param inverterId 인버터 ID
     * @param startDate  조회 시작일
     * @param endDate    조회 종료일
     */
    public List<InverterData> getInverterDataBetweenDates(Long inverterId,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        // 날짜 validation
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("시작일이 종료일보다 늦을 수 없습니다.");
        }

        return inverterDataRepository.findByInverterIdAndTimestampBetween(inverterId, startDate, endDate);
    }

    // 인버터 id로 junctionBox 조회
    public List<JunctionBoxDetailResponseDto> getJunctionBoxesById(Long inverterId) {
        Inverter inverter = inverterRepository.findByIdWithJunctionBoxes(inverterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인버터입니다."));

        List<JunctionBoxDetailResponseDto> responseDtos = new ArrayList<>();

        for (JunctionBox junctionBox : inverter.getJunctionBoxes()) {
            // 실시간 JunctionBoxData 조회
            JunctionBoxData realtimeJunctionBoxData
                    = junctionBoxDataService.findRealtimeJunctionBoxData(junctionBox.getId());

            // 금일 누적 발전량 조회
            Double todayCumulative = junctionBoxDataService.findRealtimeData(junctionBox.getId()).getCumulativeEnergy();

            // 어제까지의 전체 누적 발전량 조회
            Double previousCumulative = junctionBoxDataAccumulationService.calculateTotalCumulativeEnergyUntilYesterday(junctionBox.getId());

            // DTO 생성 및 추가
            JunctionBoxDetailResponseDto junctionBoxDetailResponseDto = JunctionBoxDetailResponseDto.fromJunctionBoxData(realtimeJunctionBoxData,
                    todayCumulative,
                    previousCumulative + todayCumulative);
            responseDtos.add(junctionBoxDetailResponseDto);
        }

        return responseDtos;
    }

    public List<InverterStatusDto> getInverterStatus() {
        List<Inverter> inverters = inverterRepository.findAll();

        List<InverterStatusDto> inverterStatusDtos = new ArrayList<>();
        for (Inverter inverter : inverters) {
            log.info("inverter_id: {}", inverter.getId());
            InverterData inverterData = inverterDataRepository.findTopByInverterIdOrderByTimestampDesc(inverter.getId())
                    .orElseThrow(() -> new RuntimeException("InverterData not found"));
            inverterStatusDtos.add(new InverterStatusDto(inverter.getId(), inverterData.getFaultStatus()));
        }
        return inverterStatusDtos;
    }
}
