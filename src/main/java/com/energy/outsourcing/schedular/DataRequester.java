package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;

import java.util.List;

public interface DataRequester {
    SinglePhaseInverterDto requestSinglePhaseData(Long inverterId);
    ThreePhaseInverterDto requestThreePhaseData(Long inverterId);
    List<JunctionBoxChannelDataDto> requestJunctionBoxData(Long junctionBoxId); // 접속함 ID를 사용
}
