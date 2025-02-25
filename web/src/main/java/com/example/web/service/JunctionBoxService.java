package com.example.web.service;

import com.example.web.entity.JunctionBox;
import com.example.web.repository.JunctionBoxRepository;
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
