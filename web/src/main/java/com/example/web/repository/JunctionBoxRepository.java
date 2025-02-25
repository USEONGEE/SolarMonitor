package com.example.web.repository;

import com.example.web.entity.JunctionBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JunctionBoxRepository extends JpaRepository<JunctionBox, Long>{
    @Query("SELECT j FROM JunctionBox j JOIN FETCH j.inverter WHERE j.inverter.id = :inverterId")
    List<JunctionBox> findByInverterId(@Param("inverterId") Long inverterId);

    @Query("SELECT j FROM JunctionBox j JOIN FETCH j.inverter")
    List<JunctionBox> findAllWithInverter();
}
