package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;


public interface DataProcessor {
    void processSinglePhaseData(SinglePhaseInverterDto data);
    void processThreePhaseData(ThreePhaseInverterDto data);
    void processJunctionBoxData(Long JunctionBoxId, JunctionBoxChannelDataDto data);
}
