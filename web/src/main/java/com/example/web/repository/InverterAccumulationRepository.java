package com.example.web.repository;

import com.example.web.entity.AccumulationType;
import com.example.web.entity.InverterAccumulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    // (추가) 조회 기간 시작 전에 가장 가까운 한 건을 가져오기 (이전 날짜의 마지막 스냅샷)
    Optional<InverterAccumulation> findTopByInverterIdAndTypeAndDateBeforeOrderByDateDesc(
            Long inverterId, AccumulationType type, LocalDateTime before
    );

    /**
     * 지정한 inverterId, type, date 범위 내에서
     * date를 내림차순 정렬 후 첫 번째 한 건만 가져온다.
     * (예: 특정 날짜 범위에 해당하는 DAILY 스냅샷 중 마지막 레코드)
     */
    Optional<InverterAccumulation> findTopByInverterIdAndTypeAndDateBetweenOrderByDateDesc(
            Long inverterId,
            AccumulationType type,
            LocalDateTime start,
            LocalDateTime end
    );



}
