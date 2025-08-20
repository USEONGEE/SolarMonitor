package com.example.web.repository;

import com.example.web.dto.InvertersDataResponseDto;
import com.example.web.entity.InverterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InverterDataRepository extends JpaRepository<InverterData, Long>{

    // 금일 최신 데이터 조회 메서드 추가
    Optional<InverterData> findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(Long inverterId, LocalDateTime start, LocalDateTime end);

    /**
     * 모든 인버터의 최신 데이터 조회
     * @return
     */
    @Query("SELECT new com.example.web.dto.InvertersDataResponseDto(" +
            "i.id, " +
            "d.currentOutput, " +       // realtimeKw에 매핑
            "d.cumulativeEnergy, " +
            "d.timestamp " + // dailyCumulativeKw에 매핑
            ") " +
            "FROM Inverter i " +
            "JOIN i.inverterDataList d " +
            "WHERE d.timestamp = (" +
            "   SELECT MAX(d2.timestamp) " +
            "   FROM InverterData d2 " +
            "   WHERE d2.inverter = i" +
            ")")
    List<InvertersDataResponseDto> findAllLatestInverterData();

    Optional<InverterData> findTopByInverterIdOrderByTimestampDesc(Long inverterId);


    Optional<InverterData> findFirstByInverterIdAndTimestampLessThanOrderByTimestampDesc(Long inverterId, LocalDateTime yesterdayEnd);

    Optional<InverterData> findFirstByInverterIdAndTimestampLessThanEqualOrderByTimestampDesc(Long inverterId, LocalDateTime timestamp);
    List<InverterData> findByInverterIdAndTimestampBetween(Long inverterId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(d) FROM InverterData d WHERE d.currentOutput > 0 AND d.inverter.id = :inverterId AND d.timestamp BETWEEN :startDate AND :endDate")
    Long countByInverterIdAndTimestampBetween(Long inverterId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 주어진 inverterId, timestamp < before 인 조건에서
     * timestamp가 가장 큰 한 건을 반환한다.
     * (보통 이전 날짜의 마지막 스냅샷을 구할 때 사용)
     */
    Optional<InverterData> findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
            Long inverterId,
            LocalDateTime before
    );
}
