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

}
