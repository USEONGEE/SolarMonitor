package com.example.web.service;

import com.example.web.entity.Inverter;
import com.example.web.entity.InverterData;
import com.example.web.repository.InverterRepository;
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
