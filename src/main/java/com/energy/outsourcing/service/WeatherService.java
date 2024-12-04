package com.energy.outsourcing.service;

import com.energy.outsourcing.entity.WeatherData;
import com.energy.outsourcing.repository.WeatherDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
            String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" + "?serviceKey=" + "UZ0z6dPfP50mYZ/xzVxDCOVxbqxUFprY8D3NT1g2zbUH2zBcBBfiXzWHoZDS6f3FIc5iVLmu4k/GpB/5I1a4BA==" +
                    "&pageNo=4&numOfRows=6&dataType=JSON&base_date=20241204&base_time=2130&nx=68&ny=141";

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            for (JsonNode item : items) {
                WeatherData weatherData = new WeatherData(item.get("baseDate").asText(),
                        item.get("baseTime").asText(),
                        item.get("category").asText(),
                        item.get("fcstDate").asText(),
                        item.get("fcstTime").asText(),
                        item.get("fcstValue").asText(),
                        item.get("nx").asInt(),
                        item.get("ny").asInt());

                if (!repository.existsById(weatherData.getBaseDate())) {
                    repository.save(weatherData);
                    log.info(weatherData.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching or saving weather data: " + e.getMessage());
        }
    }
}