package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.InvertersDataResponseDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.entity.SinglePhaseInverterData;
import com.energy.outsourcing.entity.ThreePhaseInverterData;
import com.energy.outsourcing.repository.InverterDataRepository;
import com.energy.outsourcing.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InverterDataService {
    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;

    public void saveSinglePhaseData(Long inverterId, SinglePhaseInverterDto dto) {
        Inverter inverter = inverterRepository.findById(inverterId).orElseThrow(() -> new RuntimeException("Inverter not found"));
        SinglePhaseInverterData data = SinglePhaseInverterData.fromDTO(dto);
        data.setInverter(inverter);
        data.setTimestamp(LocalDateTime.now());
        inverterDataRepository.save(data);
    }

    public void saveThreePhaseData(Long inverterId, ThreePhaseInverterDto dto) {
        Inverter inverter = inverterRepository.findById(inverterId).orElseThrow(() -> new RuntimeException("Inverter not found"));
        ThreePhaseInverterData data = ThreePhaseInverterData.fromDTO(dto);
        data.setInverter(inverter);
        data.setTimestamp(LocalDateTime.now());
        inverterDataRepository.save(data);
    }

    // TODO 나중에 인버터 갯수가 많아진다면 한 번에 조회하도록 해야함.
    public List<InvertersDataResponseDto> getAllInvertersRealtimeData() {
        return inverterRepository.findAll().stream()
                .map(inverter -> {
                    Double realtimeKw = calculateRealtimeKw(inverter);
                    Double dailyCumulativeKw = calculateDailyCumulativeKw(inverter);
                    return new InvertersDataResponseDto(inverter.getId(), realtimeKw, dailyCumulativeKw);
                })
                .collect(Collectors.toList());
    }

    private Double calculateRealtimeKw(Inverter inverter) {
        InverterData inverterData = inverterDataRepository.findByInverterId(inverter.getId())
                .orElseThrow(() -> new RuntimeException("인버터 데이터가 존재하지 않습니다."));
        // 실시간 KW 값을 계산하는 로직
        return inverterData.getCurrentOutput(); // 예: 실시간 전력 필드를 가져옴
    }

    private Double calculateDailyCumulativeKw(Inverter inverter) {
        InverterData inverterData = inverterDataRepository.findByInverterId(inverter.getId())
                .orElseThrow(() -> new RuntimeException("인버터 데이터가 존재하지 않습니다."));
        return inverterData.getCumulativeEnergy(); // 예: 당일 누적 전력 필드를 가져옴
    }

    public InverterData findOne(Long inverterId) {
        return inverterDataRepository.findByInverterId(inverterId)
                .orElseThrow(() -> new RuntimeException("인버터가 존재하지 않습니다."));
    }
}
