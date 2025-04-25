package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.Inverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InverterRepository extends JpaRepository<Inverter, Long> {

    @Query("select i from Inverter i join fetch i.junctionBoxes where i.id = :inverterId")
    Optional<Inverter> findByIdWithJunctionBoxes(Long inverterId);
}
