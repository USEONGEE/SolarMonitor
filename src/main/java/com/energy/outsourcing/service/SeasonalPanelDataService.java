package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.SeasonalPanelDataDto;
import com.energy.outsourcing.entity.SeasonalPanelData;
import com.energy.outsourcing.entity.WeatherData;
import com.energy.outsourcing.repository.SeasonalPanelDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeasonalPanelDataService {
    private final SeasonalPanelDataRepository seasonalPanelDataRepository;

    public SeasonalPanelData save(SeasonalPanelDataDto seasonalPanelDataDto) {
        SeasonalPanelData seasonalPanelData = new SeasonalPanelData(seasonalPanelDataDto);

        return seasonalPanelDataRepository.save(seasonalPanelData);
    }

    public SeasonalPanelData fetchLatest() {
        Pageable pageable = PageRequest.of(0, 1); // 가장 최근 데이터 1개만
        List<SeasonalPanelData> latestWeatherData = seasonalPanelDataRepository.findLatestWeatherData(pageable);

        if (!latestWeatherData.isEmpty()) {
            return latestWeatherData.get(0);
        } else {
            log.warn("No data available!");
            return null;
        }
    }
}
