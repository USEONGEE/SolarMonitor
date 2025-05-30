package com.example.web.service;

import com.example.web.dto.JunctionBoxDataRealtimeResponseDto;
import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.entity.JunctionBox;
import com.example.web.entity.JunctionBoxData;
import com.example.web.repository.JunctionBoxDataRepository;
import com.example.web.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JunctionBoxDataService {
    private final JunctionBoxDataRepository junctionBoxDataRepository;
    private final JunctionBoxRepository junctionBoxRepository;


    @Transactional
    public JunctionBoxData saveJunctionBoxData(Long junctionBoxId, JunctionBoxDataRequestDto dto, LocalDateTime timestamp) {
        JunctionBox junctionBox = junctionBoxRepository.findById(junctionBoxId)
                .orElseThrow(() -> new RuntimeException("JunctionBox not found"));
        JunctionBoxData junctionBoxData = JunctionBoxData.fromDTO(junctionBox, dto, timestamp);
        return junctionBoxDataRepository.save(junctionBoxData);
    }

    // 모든 접속반의 2분 내의 실시간 데이터 조회
    public List<JunctionBoxDataRealtimeResponseDto> findRealtimeDataAll() {
        List<JunctionBox> all = junctionBoxRepository.findAll();
        List<JunctionBoxDataRealtimeResponseDto> response = new ArrayList<>();

        for (JunctionBox junctionBox : all) {
            // 추출한 공통 메서드를 사용
            JunctionBoxDataRealtimeResponseDto dto = getRealtimeDataForJunctionBox(junctionBox.getId());
            response.add(dto);
        }
        return response;
    }

    // 접속반별 2분 내의 실시간 데이터 조회
    public JunctionBoxDataRealtimeResponseDto findRealtimeData(Long junctionBoxId) {
        // 추출한 공통 메서드를 사용
        return getRealtimeDataForJunctionBox(junctionBoxId);
    }

    public JunctionBoxData findRealtimeJunctionBoxData(Long junctionBoxId) {
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime startTime = timestamp.minus(2, ChronoUnit.MINUTES);
        LocalDateTime endTime = timestamp;
        return getJunctionBoxData(junctionBoxId, startTime, endTime);
    }

    private JunctionBoxDataRealtimeResponseDto getRealtimeDataForJunctionBox(Long junctionBoxId) {
        LocalDateTime midnight = LocalDate.now().atStartOfDay();
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime startTime = timestamp.minus(2, ChronoUnit.MINUTES);
        LocalDateTime endTime = timestamp;

        // 접속반의 2분 내의 데이터 중 가장 최신 데이터 조회 -> 2분 내의 데이터가 없을 경우 empty 객체 반환
        JunctionBoxData lastData = getJunctionBoxData(junctionBoxId, startTime, endTime);

        // 일일 발전량 합계 구하기
        List<JunctionBoxData> dailyData = junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, midnight, timestamp);
        double sum = dailyData.stream().mapToDouble(JunctionBoxData::getPower).sum() / 60;

        // DTO 생성 및 반환
        return new JunctionBoxDataRealtimeResponseDto(junctionBoxId, lastData.getPower(), sum);
    }

    // 주어진 시간 사이의 가장 최신 JunctionBoxData 조회
    private JunctionBoxData getJunctionBoxData(Long junctionBoxId, LocalDateTime startTime, LocalDateTime endTime) {

        JunctionBoxData lastData = junctionBoxDataRepository
                .findFirstByJunctionBoxIdAndTimestampBetweenOrderByTimestampDesc(junctionBoxId, startTime, endTime)
                .orElse(JunctionBoxData.empty());
        return lastData;
    }

    // 특정 시간 사이의 JunctionBoxData 조회
    public List<JunctionBoxData> findByJunctionBoxIdAndTimestampBetween(Long junctionBoxId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, startDateTime, endDateTime);
    }

}
