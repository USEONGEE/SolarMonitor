package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.JunctionBoxData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JunctionBoxDataRepository extends JpaRepository<JunctionBoxData, Long> {

    Optional<JunctionBoxData> findFirstByJunctionBoxIdAndTimestampBetweenOrderByTimestampDesc(
            @Param("junctionBoxId") Long junctionBoxId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT j FROM JunctionBoxData j WHERE j.junctionBox.id = :junctionBoxId " +
            "AND j.timestamp BETWEEN :startTime AND :endTime")
    List<JunctionBoxData> findByJunctionBoxIdAndTimestampBetween(
            @Param("junctionBoxId") Long junctionBoxId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

}
