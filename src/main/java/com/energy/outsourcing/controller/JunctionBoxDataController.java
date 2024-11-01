package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.JunctionBoxDataRealtimeResponseDto;
import com.energy.outsourcing.dto.JunctionBoxDataResponseDto;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.service.JunctionBoxDataService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
