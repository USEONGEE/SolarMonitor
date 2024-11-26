package com.energy.outsourcing.service;

import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InverterService {

    private final InverterRepository inverterRepository;

    // 인버터 데이터 조회
    public List<Inverter> getInverterData() {
        return inverterRepository.findAll();
    }

    // 인버터 id로 jucntionBox 조회
    public Inverter getJunctionBoxesById(Long inverterId) {
        return inverterRepository.findByIdWithJunctionBoxes(inverterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인버터입니다."));
    }

}
