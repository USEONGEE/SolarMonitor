package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.JunctionBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JunctionBoxRepository extends JpaRepository<JunctionBox, Long> {
}
