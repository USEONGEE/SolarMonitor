package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.repository.JunctionBoxChannelDataRepository;
import com.energy.outsourcing.service.InverterDataService;
import com.energy.outsourcing.service.JunctionBoxChannelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataProcessorImpl implements DataProcessor {
    private final InverterDataService inverterDataService;
    private final JunctionBoxChannelDataRepository junctionBoxChannelDataRepository;
    private final JunctionBoxChannelDataService junctionBoxChannelDataService;

    @Override
    public void processSinglePhaseData(SinglePhaseInverterDto data) {
        inverterDataService.saveSinglePhaseData(1L, data); // 단상 인버터 ID 예시
    }

    @Override
    public void processThreePhaseData(ThreePhaseInverterDto data) {
        inverterDataService.saveThreePhaseData(2L, data); // 삼상 인버터 ID 예시
    }

    @Override
    public void processJunctionBoxData(Long junctionBoxId, JunctionBoxChannelDataDto data) {
        junctionBoxChannelDataService.saveChannelData(junctionBoxId, data);
    }
}
