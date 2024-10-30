package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.InverterDetailResponseDto;
import com.energy.outsourcing.dto.InvertersDataResponseDto;
import com.energy.outsourcing.service.InverterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inverters")
@RequiredArgsConstructor
public class InverterDataController {
    private final InverterDataService inverterDataService;

    @GetMapping("/realtime/all")
    public ResponseEntity<List<InvertersDataResponseDto>>  getAllInvertersRealtimeData() {
        return ResponseEntity.ok(inverterDataService.getAllInvertersLatestData());
    }

    /**
     * 특정 인버터의 상세 데이터를 조회하는 엔드포인트
     *
     * @param inverterId 인버터 ID
     * @return 인버터 상세 데이터 DTO
     */
    @GetMapping("/{inverterId}/detail")
    public ResponseEntity<InverterDetailResponseDto>  getInverterDetail(@PathVariable Long inverterId) {
        return ResponseEntity.ok(inverterDataService.getInverterDetail(inverterId));
    }
}

