package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import com.example.web.entity.InverterData;
import com.example.web.entity.JunctionBoxData;
import com.example.web.entity.SeasonalPanelData;
import com.example.web.service.InverterDataService;
import com.example.web.service.JunctionBoxDataService;
import com.example.web.service.SeasonalPanelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DataProcessorImpl implements DataProcessor {
    private final InverterDataService inverterDataService;
    private final JunctionBoxDataService junctionBoxDataService;
    private final SeasonalPanelDataService seasonalPanelDataService;

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

    @Override
    public SeasonalPanelData processSeasonalPanelData(SeasonalPanelDataDto seasonalPanelDataDto) {
        return seasonalPanelDataService.save(seasonalPanelDataDto);
    }

}
