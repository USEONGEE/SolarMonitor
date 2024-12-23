package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.SeasonalPanelData;
import com.energy.outsourcing.entity.WeatherData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SeasonalPanelDataRepository extends JpaRepository<SeasonalPanelData, Long> {
    @Query("SELECT s FROM SeasonalPanelData s ORDER BY s.createdDate DESC")
    List<SeasonalPanelData> findLatestWeatherData(Pageable pageable);

}
