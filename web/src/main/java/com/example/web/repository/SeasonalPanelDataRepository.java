package com.example.web.repository;

import com.example.web.entity.SeasonalPanelData;
import com.example.web.entity.WeatherData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface SeasonalPanelDataRepository extends JpaRepository<SeasonalPanelData, Long> {
    @Query("SELECT s FROM SeasonalPanelData s ORDER BY s.createdDate DESC")
    List<SeasonalPanelData> findLatestWeatherData(Pageable pageable);

    // 생성 시간이 주어진 시간 사이에
    List<SeasonalPanelData> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<SeasonalPanelData>
    findByInverterIdAndCreatedDateBetween(Long inverterId,
                                          LocalDateTime start,
                                          LocalDateTime end);
}
