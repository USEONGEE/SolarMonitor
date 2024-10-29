package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.InvertersDataResponseDto;
import com.energy.outsourcing.service.InverterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inverters")
@RequiredArgsConstructor
public class InverterDataController {
    private final InverterDataService inverterDataService;

    @GetMapping("/realtime/all")
    public List<InvertersDataResponseDto> getAllInvertersRealtimeData() {
        return inverterDataService.getAllInvertersLatestData();
    }

    // TODO 전체 누적 발전량도 포함해ㅇ함
//    @GetMapping("/realtime/{inverterId}")
//    public InvertersDataResponseDto getInverterRealtimeData(Long inverterId) {
//        return inverterDataService.findOne(inverterId);
//    }
}

