package com.example.web.controller;

import com.example.web.dto.SeasonalPanelDataResponseDto;
import com.example.web.entity.SeasonalPanelData;
import com.example.web.service.SeasonalPanelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seasonal-panel")
public class SeasonalPanelController {

    private final SeasonalPanelDataService seasonalPanelDataService;


    @GetMapping("/data")
    public List<SeasonalPanelDataResponseDto> getSeasonalPanelData(@RequestParam(value = "startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDateTime,
                                                                   @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDateTime) {
        List<SeasonalPanelData> seasonalPanelDataBetweenDates = seasonalPanelDataService.getSeasonalPanelDataBetweenDates(startDateTime, endDateTime);
        return seasonalPanelDataBetweenDates.stream()
                .map(SeasonalPanelDataResponseDto::from)
                .collect(Collectors.toList());
    }
}
