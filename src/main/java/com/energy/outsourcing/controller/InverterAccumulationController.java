package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.InverterAccumulationDto;
import com.energy.outsourcing.service.InverterAccumulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inverter")
@RequiredArgsConstructor
public class InverterAccumulationController {

    private final InverterAccumulationService accumulationService;

    /**
     * 시간별 발전량 조회 엔드포인트
     * @param inverterId 인버터 ID
     * @param date 날짜 (선택 사항, 기본값은 오늘)
     * @return 시간별 발전량 리스트
     */
    @GetMapping("/{inverterId}/hourly")
    public ResponseEntity<List<InverterAccumulationDto>>  getHourlyGeneration(
            @PathVariable Long inverterId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }

        return ResponseEntity.ok(accumulationService.getHourlyAccumulations(inverterId, date));
    }

    /**
     * 일별 발전량 조회 엔드포인트
     * @param inverterId 인버터 ID
     * @param month 월 (선택 사항, 기본값은 현재 월)
     * @return 일별 발전량 리스트
     */
    @GetMapping("/{inverterId}/daily")
    public ResponseEntity<List<InverterAccumulationDto>>  getDailyGeneration(
            @PathVariable Long inverterId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {

        if (month == null) {
            month = LocalDate.now().withDayOfMonth(1);
        } else {
            month = month.withDayOfMonth(1);
        }

        return ResponseEntity.ok(accumulationService.getDailyAccumulations(inverterId, month));
    }

    /**
     * 월별 발전량 조회 엔드포인트
     * @param inverterId 인버터 ID
     * @param year 연도 (선택 사항, 기본값은 현재 연도)
     * @return 월별 발전량 리스트
     */
    @GetMapping("/{inverterId}/monthly")
    public ResponseEntity<List<InverterAccumulationDto>> getMonthlyGeneration(
            @PathVariable Long inverterId,
            @RequestParam(required = false) Integer year) {

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        return ResponseEntity.ok(accumulationService.getMonthlyAccumulations(inverterId, year));
    }
}
