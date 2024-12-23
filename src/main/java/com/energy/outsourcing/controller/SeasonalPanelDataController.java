package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.SeasonalPanelDataDto;
import com.energy.outsourcing.entity.SeasonalPanelData;
import com.energy.outsourcing.service.SeasonalPanelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/panel-weather-data")
public class SeasonalPanelDataController {
    private final SeasonalPanelDataService seasonalPanelDataService;

    @RequestMapping("/latest")
    public ResponseEntity<SeasonalPanelDataDto> getLatestPanelWeatherData() {
        SeasonalPanelData seasonalPanelData = seasonalPanelDataService.fetchLatest();
        if (seasonalPanelData != null) {
            SeasonalPanelDataDto seasonalPanelDataDto = new SeasonalPanelDataDto(seasonalPanelData);
            return ResponseEntity.ok(seasonalPanelDataDto);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
