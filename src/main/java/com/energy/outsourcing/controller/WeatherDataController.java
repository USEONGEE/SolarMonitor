package com.energy.outsourcing.controller;

import com.energy.outsourcing.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather-data")
public class WeatherDataController {

    private final WeatherService weatherService;

    @GetMapping("/latest")
    public ResponseEntity<String> getLatestFcstValue() {
        String latestValue = weatherService.getLatestFcstValue();

        if (latestValue != null) {
            return ResponseEntity.ok(latestValue);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No forecast data available");
        }
    }
}
