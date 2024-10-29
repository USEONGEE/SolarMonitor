package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.InverterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InverterDataRepository extends JpaRepository<InverterData, Long>{

    @Query("SELECT id FROM InverterData id JOIN FETCH id.inverter WHERE id.inverter.id = :inverterId")
    Optional<InverterData> findByInverterId(@Param("inverterId") Long inverterId);

    @Query("SELECT id FROM InverterData id JOIN FETCH id.inverter WHERE id.inverter.id = :inverterId AND id.timestamp < :yesterdayEnd ORDER BY id.timestamp DESC")
    Optional<InverterData> findLastInverterDataByInverterId(@Param("inverterId")Long inverterId, @Param("yesterdayEnd")LocalDateTime yesterdayEnd);
}
