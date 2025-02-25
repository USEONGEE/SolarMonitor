package com.example.web.service;

import com.example.web.entity.WeatherData;
import com.example.web.repository.WeatherDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {
    private final WeatherDataRepository repository;

    public WeatherService(WeatherDataRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void fetchAndSaveWeatherData() {
        try {
            log.info("날씨 데이터를 fetch 시작");
            LocalDateTime now = LocalDateTime.now().minusHours(1); // 한 시간 전 시간으로 설정
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 현재 날짜 (YYYYMMDD 형식)
            String baseTime = now.format(DateTimeFormatter.ofPattern("HH")) + "00"; // 현재 시간에서 분을 제외한 시간 (HH00 형식)

            String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" +
                    "?serviceKey=" + "UZ0z6dPfP50mYZ/xzVxDCOVxbqxUFprY8D3NT1g2zbUH2zBcBBfiXzWHoZDS6f3FIc5iVLmu4k/GpB/5I1a4BA==" +
                    "&pageNo=4&numOfRows=6&dataType=JSON&base_date=" + baseDate +
                    "&base_time=" + baseTime +
                    "&nx=68&ny=141";

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            for (JsonNode item : items) {
                // WeatherData 생성
                WeatherData weatherData = new WeatherData(
                        item.get("baseDate").asText(),
                        item.get("baseTime").asText(),
                        item.get("category").asText(),
                        item.get("fcstDate").asText(),
                        item.get("fcstTime").asText(),
                        item.get("fcstValue").asText(),
                        item.get("nx").asInt(),
                        item.get("ny").asInt()
                );

                // uniqueId를 사용해 중복 확인
                if (!repository.existsById(weatherData.getUniqueId())) {
                    repository.save(weatherData);
                    log.info("Saved: {}", weatherData);
                } else {
                    log.info("Skipped (Duplicate): {}", weatherData.getUniqueId());
                }
            }
            log.info("날씨 데이터를 fetch 종료");
        } catch (Exception e) {
            System.err.println("Error fetching or saving weather data: " + e.getMessage());
        }
    }

    // 가장 최근 fcstValue 반환
    public String getLatestFcstValue() {
        Pageable pageable = PageRequest.of(0, 1); // 가장 최근 데이터 1개만
        List<WeatherData> latestData = repository.findLatestWeatherData(pageable);

        if (!latestData.isEmpty()) {
            WeatherData weatherData = latestData.get(0);
            log.info("Latest fcstValue: {}", weatherData.getFcstValue());
            return weatherData.getFcstValue();
        } else {
            log.warn("No data available!");
            return null;
        }
    }
}