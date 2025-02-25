package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import com.example.web.entity.InverterData;
import com.example.web.entity.JunctionBoxData;
import com.example.web.entity.SeasonalPanelData;

import java.time.LocalDateTime;


public interface DataProcessor {
    InverterData processSinglePhaseData(SinglePhaseInverterDto data, LocalDateTime timestamp);
    InverterData processThreePhaseData(ThreePhaseInverterDto data, LocalDateTime timestamp);

    JunctionBoxData processJunctionBoxData(Long junctionBoxId, JunctionBoxDataRequestDto junctionBoxDataList, LocalDateTime timestamp);

    SeasonalPanelData processSeasonalPanelData(SeasonalPanelDataDto seasonalPanelDataDto);
}
