package com.example.web.service;

import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.entity.SeasonalPanelData;
import com.example.web.repository.SeasonalPanelDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SeasonalPanelDataService {
    private final SeasonalPanelDataRepository seasonalPanelDataRepository;

    @Transactional
    public SeasonalPanelData save(SeasonalPanelDataDto seasonalPanelDataDto) {
        SeasonalPanelData seasonalPanelData = new SeasonalPanelData(seasonalPanelDataDto);

        return seasonalPanelDataRepository.save(seasonalPanelData);
    }

    public List<SeasonalPanelData> getSeasonalPanelDataBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        // 날짜 validation
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("시작일이 종료일보다 늦을 수 없습니다.");
        }

        return seasonalPanelDataRepository.findByCreatedDateBetween(startDate, endDate);
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
