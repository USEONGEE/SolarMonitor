package com.example.web.entity;

import com.example.web.config.LocalDateTimeAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeatherData extends BaseTimeEntity {

    @Id
    private String uniqueId; // UNIQUE 키 생성 (baseDate + baseTime + category)

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime baseDateTime; // baseDate + baseTime

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime fcstDateTime; // fcstDate + fcstTime

    private String category;
    private String fcstValue;
    private int nx;
    private int ny;

    public WeatherData(String baseDate, String baseTime, String category,
                       String fcstDate, String fcstTime, String fcstValue, int nx, int ny) {
        this.uniqueId = baseDate + baseTime + category;
        this.baseDateTime = LocalDateTime.parse(baseDate + baseTime,
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        this.fcstDateTime = LocalDateTime.parse(fcstDate + fcstTime,
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        this.category = category;
        this.fcstValue = fcstValue;
        this.nx = nx;
        this.ny = ny;
    }
}
