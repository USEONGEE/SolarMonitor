package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.InverterDetailResponseDto;
import com.energy.outsourcing.dto.InvertersDataResponseDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.*;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
import com.energy.outsourcing.repository.InverterDataRepository;
import com.energy.outsourcing.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InverterDataService {
    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;

    /**
     * 단상 인버터 데이터 저장
     * @param inverterId
     * @param dto
     */
    public void saveSinglePhaseData(Long inverterId, SinglePhaseInverterDto dto) {
        Inverter inverter = inverterRepository.findById(inverterId).orElseThrow(() -> new RuntimeException("Inverter not found"));
        SinglePhaseInverterData data = SinglePhaseInverterData.fromDTO(dto);
        data.setInverter(inverter);
        data.setTimestamp(LocalDateTime.now());
        inverterDataRepository.save(data);
    }

    /**
     * 삼상 인버터 데이터 저장
     * @param inverterId
     * @param dto
     */
    public void saveThreePhaseData(Long inverterId, ThreePhaseInverterDto dto) {
        Inverter inverter = inverterRepository.findById(inverterId).orElseThrow(() -> new RuntimeException("Inverter not found"));
        ThreePhaseInverterData data = ThreePhaseInverterData.fromDTO(dto);
        data.setInverter(inverter);
        data.setTimestamp(LocalDateTime.now());
        inverterDataRepository.save(data);
    }

    /**
     * 모든 인버터의 최신 데이터 조회
     * @return
     */
    // TODO 나중에 인버터 갯수가 많아진다면 한 번에 조회하도록 해야함.
    public List<InvertersDataResponseDto> getAllInvertersLatestData() {
        return inverterDataRepository.findAllLatestInverterData();
    }

    /**
     * 인버터 상세 조회
     * @param inverterId
     * @return
     */
    @Transactional(readOnly = true)
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
                .sum();

        // DTO 생성 및 반환
        return InverterDetailResponseDto.fromInverterData(latestData, totalMonthlyCumulativeEnergy);
    }

}
