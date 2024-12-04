package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherDataRepository extends JpaRepository<WeatherData, String> {
}

