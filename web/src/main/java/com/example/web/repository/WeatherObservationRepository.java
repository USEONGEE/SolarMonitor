package com.example.web.repository;

import com.example.web.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Integer> {
    // 날씨 데이터 최신 데이터 조회
    Optional<WeatherObservation> findTopByOrderByTmDesc();
}
