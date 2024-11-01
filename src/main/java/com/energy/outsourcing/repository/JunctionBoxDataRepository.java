package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.JunctionBoxData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JunctionBoxDataRepository extends JpaRepository<JunctionBoxData, Long> {

    // 두 시간 사이의 데이터 가져오기
    @Query("SELECT jbd FROM JunctionBoxData jbd join fetch jbd.junctionBox jb WHERE jb.id = :junctionBoxId AND jbd.timestamp BETWEEN :start AND :end")
    List<JunctionBoxData> findByJunctionBoxIdAndTimestampBetween(Long junctionBoxId, LocalDateTime start, LocalDateTime end);

    Optional<JunctionBoxData> findTopByJunctionBoxIdOrderByTimestampDesc(Long junctionBoxId);
}
