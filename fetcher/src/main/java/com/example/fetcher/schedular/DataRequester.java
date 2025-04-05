package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;

public interface DataRequester {
    SinglePhaseInverterDto requestSinglePhaseData(Long inverterId);
    ThreePhaseInverterDto requestThreePhaseData(Long inverterId);
    JunctionBoxDataRequestDto requestJunctionBoxData(Long inverterId);

    SeasonalPanelDataDto requestSeasonal();
}
