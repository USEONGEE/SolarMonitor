package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.JunctionBoxDataRequestDto;
import com.energy.outsourcing.dto.SeasonalPanelDataDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.entity.SeasonalPanelData;

public interface DataRequester {
    SinglePhaseInverterDto requestSinglePhaseData(Long inverterId);
    ThreePhaseInverterDto requestThreePhaseData(Long inverterId);
    JunctionBoxDataRequestDto requestJunctionBoxData(Long junctionBoxId); // 접속함 ID를 사용
    // TODO 일사량 요청

    SeasonalPanelDataDto requestJunctionBox();
}
