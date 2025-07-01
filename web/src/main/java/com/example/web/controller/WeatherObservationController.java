package com.example.web.controller;

import com.example.web.entity.WeatherObservation;
import com.example.web.service.WeatherObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather-observations")
public class WeatherObservationController {

    private final WeatherObservationService weatherObservationService;

    @GetMapping
    public ResponseEntity<WeatherObservation> getLatestWeatherObservation() {
        WeatherObservation latestObservation = weatherObservationService.getLatestWeatherObservation();

        return ResponseEntity.ok(latestObservation);
    }

}
