package com.example.web.controller;

import com.example.web.dto.JunctionBoxDataRealtimeResponseDto;
import com.example.web.dto.JunctionBoxDataResponseDto;
import com.example.web.entity.JunctionBoxData;
import com.example.web.service.JunctionBoxDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/junctionBoxData")
public class JunctionBoxDataController {
    private final JunctionBoxDataService junctionBoxDataService;

    /**
     * 접속반 데이터 조회, 주어진 시간 안에서
     * @param junctionBoxId 접속반 ID
     * @param startDateTime 시작 시간
     * @param endDateTime 끝 시간
     * @return
     */
    @GetMapping("/{junctionBoxId}/data")
    public ResponseEntity<List<JunctionBoxDataResponseDto>> getInverterData(@PathVariable Long junctionBoxId,
                                                                            @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDateTime,
                                                                            @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDateTime) {
        List<JunctionBoxData> junctionBoxDataList
                = junctionBoxDataService.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, startDateTime, endDateTime);

        List<JunctionBoxDataResponseDto> list = junctionBoxDataList.stream()
                .map(data -> new JunctionBoxDataResponseDto(data.getJunctionBox().getId(), data.getPower(), data.getTimestamp()))
                .toList();

        return ResponseEntity.ok(list);
    }

    /**
     * 접속반 실시간 데이터 조회
     * @param junctionBoxId
     * @return
     */

    // TODO  접속반 하나에 ch이 있는게 아니라 하나의 인버터에 2개의 array가 있고 그게 접속만 ch이다.
    @GetMapping("/{junctionBoxId}/realtime")
    public ResponseEntity<JunctionBoxDataRealtimeResponseDto> getRealtimeData(@PathVariable Long junctionBoxId) {
        JunctionBoxDataRealtimeResponseDto realtimeData = junctionBoxDataService.findRealtimeData(junctionBoxId);
        return ResponseEntity.ok(realtimeData);
    }

    @GetMapping("/realtime/all")
    public ResponseEntity<List<JunctionBoxDataRealtimeResponseDto>> getRealtimeDataAll() {
        List<JunctionBoxDataRealtimeResponseDto> realtimeDataAll = junctionBoxDataService.findRealtimeDataAll();
        return ResponseEntity.ok(realtimeDataAll);
    }
}
