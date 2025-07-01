package com.example.web.controller;

import com.example.web.entity.WeatherData2;
import com.example.web.repository.WeatherData2Repository;
import com.example.web.service.Weather2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather-data2")
public class Weather2DataController {

    private final Weather2Service weather2Service;
    private final WeatherData2Repository weatherData2Repository;

    /**
     * 최신 관측 데이터 1건 조회
     */
    @GetMapping("/latest")
    public ResponseEntity<WeatherData2> getLatest() {
        // 가장 최근 obsTime 기준 내림차순 정렬하여 1건 반환
        List<WeatherData2> all = weatherData2Repository.findAll();
        return all.stream()
                .max(Comparator.comparing(WeatherData2::getObsTime))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    /**
     * 관측 데이터 직접 수집 및 저장 트리거
     * @param tm 관측 시각(YYMMDDHHMI) 예: 202506171100
     */
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchAndSave(@RequestParam String tm) {
        try {
            weather2Service.fetchAndSave(tm);
            return ResponseEntity.ok("수집 및 저장 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("수집 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 관측소, 시각 데이터 조회
     */
    @GetMapping("/by-station-time")
    public ResponseEntity<WeatherData2> getByStationAndTime(
            @RequestParam Integer stationId,
            @RequestParam String obsTime
    ) {
        Optional<WeatherData2> result = weatherData2Repository.findAll().stream()
                .filter(wd -> wd.getStationId().equals(stationId) && wd.getObsTime().equals(obsTime))
                .findFirst();
        return result.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    /**
     * 전체 데이터 목록 조회 (페이징 없이)
     */
    @GetMapping("/all")
    public List<WeatherData2> getAll() {
        return weatherData2Repository.findAll();
    }
}
