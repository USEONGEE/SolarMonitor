package com.example.web.service;

import com.example.web.entity.WeatherObservation;
import com.example.web.repository.WeatherObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherObservationService {
    private final WeatherObservationRepository weatherObservationRepository;


    // 날씨 데이터 최신 데이터 조회
    public WeatherObservation getLatestWeatherObservation() {
        return weatherObservationRepository.findTopByOrderByTmDesc()
                .orElseThrow(() -> new RuntimeException("No weather observation data found"));
    }

}
