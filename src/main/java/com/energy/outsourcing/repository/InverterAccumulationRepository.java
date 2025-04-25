package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.InverterAccumulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InverterAccumulationRepository extends JpaRepository<InverterAccumulation, Long> {
    List<InverterAccumulation> findByInverterIdAndType(Long inverterId, AccumulationType type);

    /**
     * Finds all InverterAccumulation records for a specific inverter, type, and within a date range.
     *
     * @param inverterId The ID of the inverter.
     * @param type The type of accumulation (DAILY or MONTHLY).
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A list of InverterAccumulation records matching the criteria.
     */
    List<InverterAccumulation> findByInverterIdAndTypeAndDateBetween(
            Long inverterId,
            AccumulationType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // 추가 메서드
    List<InverterAccumulation> findByTypeAndDateBetween(AccumulationType type, LocalDateTime startDate, LocalDateTime endDate);

    List<InverterAccumulation> findByType(AccumulationType type);

}
