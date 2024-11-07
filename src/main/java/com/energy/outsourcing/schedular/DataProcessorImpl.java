package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.JunctionBoxDataRequestDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.service.InverterDataService;
import com.energy.outsourcing.service.JunctionBoxDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataProcessorImpl implements DataProcessor {
    private final InverterDataService inverterDataService;
    private final JunctionBoxDataService junctionBoxDataService;

    @Override
    public InverterData processSinglePhaseData(SinglePhaseInverterDto data, LocalDateTime timestamp) {
        return inverterDataService.saveSinglePhaseData(1L, data,  timestamp); // 단상 인버터 ID 예시
    }

    @Override
    public InverterData processThreePhaseData(ThreePhaseInverterDto data, LocalDateTime timestamp) {
        return inverterDataService.saveThreePhaseData(2L, data,  timestamp); // 삼상 인버터 ID 예시
    }

    @Override
    public JunctionBoxData processJunctionBoxData(Long junctionBoxId, JunctionBoxDataRequestDto dto, LocalDateTime timestamp) {
        return junctionBoxDataService.saveJunctionBoxData(junctionBoxId, dto, timestamp);
    }

}
