package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.JunctionBoxDataRequestDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.entity.JunctionBoxData;

import java.time.LocalDateTime;
import java.util.List;


public interface DataProcessor {
    InverterData processSinglePhaseData(SinglePhaseInverterDto data, LocalDateTime timestamp);
    InverterData processThreePhaseData(ThreePhaseInverterDto data, LocalDateTime timestamp);

    JunctionBoxData processJunctionBoxData(Long junctionBoxId, JunctionBoxDataRequestDto junctionBoxDataList, LocalDateTime timestamp);


}
