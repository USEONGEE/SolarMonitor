package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.entity.JunctionBoxChannelData;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.repository.JunctionBoxChannelDataRepository;
import com.energy.outsourcing.service.InverterDataService;
import com.energy.outsourcing.service.JunctionBoxChannelDataService;
import com.energy.outsourcing.service.JunctionBoxDataService;
import com.energy.outsourcing.service.JunctionBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataProcessorImpl implements DataProcessor {
    private final InverterDataService inverterDataService;
    private final JunctionBoxChannelDataService junctionBoxChannelDataService;
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
    public JunctionBoxChannelData processJunctionBoxChannelData(Long junctionBoxId, JunctionBoxChannelDataDto data, LocalDateTime timestamp) {
        return junctionBoxChannelDataService.saveChannelData(junctionBoxId, data,  timestamp);
    }

    @Override
    public JunctionBoxData processJunctionBoxData(Long junctionBoxId, List<JunctionBoxChannelDataDto> junctionBoxDataList, LocalDateTime timestamp) {
        return junctionBoxDataService.saveChannelDataList(junctionBoxId, junctionBoxDataList,  timestamp);
    }
}
