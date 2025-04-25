package com.energy.outsourcing.repository;


import com.energy.outsourcing.entity.WeatherData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, String> {

    // 가장 최근 fcstDateTime을 가진 데이터 가져오기
    @Query("SELECT w FROM WeatherData w ORDER BY w.fcstDateTime DESC")
    List<WeatherData> findLatestWeatherData(Pageable pageable);

}

