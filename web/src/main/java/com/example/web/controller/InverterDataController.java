package com.example.web.controller;

import com.example.web.dto.InverterDetailResponseDto;
import com.example.web.dto.InverterDto;
import com.example.web.dto.InvertersDataResponseDto;
import com.example.web.dto.JunctionBoxDetailResponseDto;
import com.example.web.entity.Inverter;
import com.example.web.entity.InverterData;
import com.example.web.service.InverterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inverters")
@RequiredArgsConstructor
public class InverterDataController {
    private final InverterDataService inverterDataService;

    /**
     * 모든 인버터의 실시간 데이터를 조회하는 엔드포인트
     * @return 인버터 실시간 데이터 리스트
     */
    @GetMapping("/realtime/all")
    public ResponseEntity<List<InvertersDataResponseDto>>  getAllInvertersRealtimeData() {
        return ResponseEntity.ok(inverterDataService.getAllInvertersLatestData());
    }

    /**
     * 특정 인버터의 상세 데이터를 조회하는 엔드포인트
     * @param inverterId 인버터 ID
     * @return 인버터 상세 데이터 DTO
     */
    @GetMapping("/{inverterId}/detail")
    public ResponseEntity<InverterDetailResponseDto>  getInverterDetail(@PathVariable Long inverterId) {
        return ResponseEntity.ok(inverterDataService.getInverterDetail(inverterId));
    }

    @GetMapping("/{inverterId}/junction-boxes")
    public ResponseEntity<List<JunctionBoxDetailResponseDto>> getJunctionBoxesById(
            @PathVariable Long inverterId
    ) {
        List<JunctionBoxDetailResponseDto> responseDtos = inverterDataService.getJunctionBoxesById(inverterId);
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 특정 인버터의 특정 기간 데이터 조회
     * @param inverterId 인버터 아이디
     * @param startDateTime 조회 시작 시간
     * @param endDateTime 조회 종료 시간
     * @return
     */
    @GetMapping("/{inverterId}/data")
    public ResponseEntity<List<InvertersDataResponseDto>> getInverterData(@PathVariable Long inverterId,
                                                                          @RequestParam(value = "startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDateTime,
                                                                          @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDateTime) {
        List<InverterData> inverterDataBetweenDates = inverterDataService.getInverterDataBetweenDates(inverterId, startDateTime, endDateTime);
        return ResponseEntity.ok(inverterDataBetweenDates.stream()
                .map(data -> new InvertersDataResponseDto(data.getInverter().getId(), data.getCurrentOutput(), data.getCumulativeEnergy(), data.getTimestamp()))
                .collect(Collectors.toList()));
    }
}

