package com.example.web.repository;

import com.example.web.entity.AccumulationType;
import com.example.web.entity.JunctionBoxDataAccumulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JunctionBoxDataAccumulationRepository extends JpaRepository<JunctionBoxDataAccumulation, Long> {
    // 특정 월을 입력받으면 해당 월의 일별 마지막 누적 저장량을 반환
    @Query("""
    SELECT jba
    FROM JunctionBoxDataAccumulation jba
    WHERE jba.timestamp BETWEEN :startDate AND :endDate
      AND jba.type = :accumulationType
      AND jba.junctionBox.id = :junctionBoxId
    ORDER BY jba.timestamp DESC""")
    List<JunctionBoxDataAccumulation> findByTimestampBetweenAndTypeAndJunctionBoxId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("accumulationType") AccumulationType accumulationType,
            @Param("junctionBoxId") Long junctionBoxId
    );

    @Query("""
    SELECT jba
    FROM JunctionBoxDataAccumulation jba
    WHERE jba.junctionBox.id = :junctionBoxId
      AND jba.type = :accumulationType""")
    List<JunctionBoxDataAccumulation> findAllByJunctionBoxIdAndType(Long junctionBoxId, AccumulationType accumulationType);

}
