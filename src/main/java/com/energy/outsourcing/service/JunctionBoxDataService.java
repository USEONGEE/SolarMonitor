package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.JunctionBoxDataRealtimeResponseDto;
import com.energy.outsourcing.dto.JunctionBoxDataRequestDto;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.repository.JunctionBoxDataRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
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

    public List<JunctionBoxDataRealtimeResponseDto> findRealtimeDataAll() {
        List<JunctionBox> all = junctionBoxRepository.findAll();
        List<JunctionBoxDataRealtimeResponseDto> response = new ArrayList<>();

        LocalDateTime midnight = LocalDate.now().atStartOfDay();
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime startTime = timestamp.minus(2, ChronoUnit.MINUTES);
        LocalDateTime endTime = timestamp;

        // ahems
        for (JunctionBox junctionBox : all) {
            // 접속반의 2분 내의 데이터 중 가장 최신 데이터 조회 -> 2분 내의 데이터가 없을 경우 empty 객체 반환
            JunctionBoxData lastData = junctionBoxDataRepository.findFirstByJunctionBoxIdAndTimestampBetweenOrderByTimestampDesc(junctionBox.getId(), startTime, endTime)
                    .orElse(JunctionBoxData.empty());

            List<JunctionBoxData> byJunctionBoxIdAndTimestampBetween = junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBox.getId(), midnight, timestamp);

            double sum = byJunctionBoxIdAndTimestampBetween.stream()
                    .mapToDouble(JunctionBoxData::getPower)
                    .sum()/ 60; // 일일 발전량 합계 구하기

            response.add(new JunctionBoxDataRealtimeResponseDto(junctionBox.getId(), lastData.getPower(), sum));
        }
        return response;
    }
    public JunctionBoxDataRealtimeResponseDto findRealtimeData(Long junctionBoxId) {
        LocalDateTime midnight = LocalDate.now().atStartOfDay();
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime startTime = timestamp.minus(2, ChronoUnit.MINUTES);
        LocalDateTime endTime = timestamp;

        // 접속반의 2분 내의 데이터 중 가장 최신 데이터 조회 -> 2분 내의 데이터가 없을 경우 empty 객체 반환
        JunctionBoxData lastData = junctionBoxDataRepository
                .findFirstByJunctionBoxIdAndTimestampBetweenOrderByTimestampDesc(junctionBoxId, startTime, endTime)
                .orElse(JunctionBoxData.empty());

        List<JunctionBoxData> dailyData = junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, midnight, timestamp);
        double sum = dailyData.stream().mapToDouble(JunctionBoxData::getPower).sum() / 60; // 일일 발전량 합계 구하기

        return new JunctionBoxDataRealtimeResponseDto(junctionBoxId, lastData.getPower(), sum);
    }


    public List<JunctionBoxData> findByJunctionBoxIdAndTimestampBetween(Long junctionBoxId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, startDateTime, endDateTime);
    }

}
