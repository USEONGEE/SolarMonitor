package com.energy.outsourcing.service;

import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JunctionBoxService {

    private final JunctionBoxRepository junctionBoxRepository;

    public List<JunctionBox> findByInverterId(Long inverterId) {
        return junctionBoxRepository.findByInverterId(inverterId);
    }

    public List<JunctionBox> findAllWithInverter() {
        return junctionBoxRepository.findAllWithInverter();
    }
}
