package com.example.web.service;

import com.example.web.dto.*;
import com.example.web.entity.*;
import com.example.web.repository.InverterAccumulationRepository;
import com.example.web.repository.InverterDataRepository;
import com.example.web.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    // TODO 나중에 인버터 갯수가 많아진다면 한 번에 조회하도록 해야함.
    public List<InvertersDataResponseDto> getAllInvertersLatestData() {
        return inverterDataRepository.findAllLatestInverterData();
    }

    /**
     * 인버터 상세 조회
     *
     * @param inverterId
     */
    @Transactional
    public InverterDetailResponseDto getInverterDetail(Long inverterId) {
        // 인버터 존재 여부 확인
        Inverter inverter = inverterRepository.findById(inverterId)
                .orElseThrow(() -> new RuntimeException("인버터가 존재하지 않습니다. ID: " + inverterId));

        // 최신 InverterData 조회
        InverterData latestData = inverterDataRepository.findTopByInverterIdOrderByTimestampDesc(inverterId)
                .orElseThrow(() -> new RuntimeException("인버터의 데이터가 존재하지 않습니다. ID: " + inverterId));

        // 월별 누적 발전량 합산
        Double totalMonthlyCumulativeEnergy = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                        inverterId,
                        AccumulationType.MONTHLY,
                        LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0),
                        LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59, 999999999)
                ).stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum() + latestData.getCumulativeEnergy();

        // DTO 생성 및 반환
        return InverterDetailResponseDto.fromInverterData(latestData, totalMonthlyCumulativeEnergy);
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
            InverterData inverterData = inverterDataRepository.findTopByInverterIdOrderByTimestampDesc(inverter.getId())
                    .orElseThrow(() -> new RuntimeException("InverterData not found"));
            inverterStatusDtos.add(new InverterStatusDto(inverter.getId(), inverterData.getFaultStatus()));
        }
        return inverterStatusDtos;
    }
}
