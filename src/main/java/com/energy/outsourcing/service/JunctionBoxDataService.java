package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.dto.JunctionBoxDataRealtimeResponseDto;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.repository.JunctionBoxDataRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JunctionBoxDataService {

    private final JunctionBoxRepository junctionBoxRepository;
    private final JunctionBoxDataRepository junctionBoxDataRepository;

    /**
     * 접속반 데이터 저장
     */
    @Transactional
    public JunctionBoxData saveChannelDataList(Long junctionBoxId, List<JunctionBoxChannelDataDto> junctionBoxDataList, LocalDateTime timestamp) {
        JunctionBox junctionBox = junctionBoxRepository.findById(junctionBoxId).orElseThrow(() -> new IllegalArgumentException("접속함이 존재하지 않습니다."));

        Double totalVoltage = 0.0;
        Double totalCurrent = 0.0;
        Double power = 0.0;

        for (JunctionBoxChannelDataDto channelData : junctionBoxDataList) {
            totalVoltage += channelData.getVoltage();
            totalCurrent += channelData.getCurrent();
            power += channelData.getVoltage() * channelData.getCurrent();
        }

        // 접속반 계소 데이터
        JunctionBoxData junctionBoxData = new JunctionBoxData(totalVoltage, totalCurrent, power, junctionBox, timestamp);
        return junctionBoxDataRepository.save(junctionBoxData);
    }

    /**
     * 접속반 데이터 조회, 주어진 사긴 하에서
     * @param junctionBoxId 접속반 ID
     * @param start 시작 시간
     * @param end 끝 시간
     * @return
     */
    public List<JunctionBoxData> findByJunctionBoxIdAndTimestampBetween(Long junctionBoxId, LocalDateTime start, LocalDateTime end) {
        return junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, start, end);
    }

    public List<JunctionBoxDataRealtimeResponseDto> findRealtimeDataAll() {
        List<JunctionBoxDataRealtimeResponseDto> dtos = new ArrayList<>();
        List<JunctionBox> junctionBoxList = junctionBoxRepository.findAll();
        for (JunctionBox junctionBox : junctionBoxList) {
            Long junctionBoxId = junctionBox.getId();
            JunctionBoxData junctionBoxData = junctionBoxDataRepository.findTopByJunctionBoxIdOrderByTimestampDesc(junctionBoxId)
                    .orElseThrow(() -> new IllegalArgumentException("접속함 데이터가 존재하지 않습니다."));
            JunctionBoxDataRealtimeResponseDto junctionBoxDataRealtimeResponseDto
                    = createJunctionBoxDataRealtimeResponseDto(junctionBoxId, junctionBoxData);
            dtos.add(junctionBoxDataRealtimeResponseDto);
        }

        return dtos;
    }

    public JunctionBoxDataRealtimeResponseDto findRealtimeData(Long junctionBoxId) {
        JunctionBoxData junctionBoxData = junctionBoxDataRepository.findTopByJunctionBoxIdOrderByTimestampDesc(junctionBoxId)
                .orElseThrow(() -> new IllegalArgumentException("접속함 데이터가 존재하지 않습니다."));
        return createJunctionBoxDataRealtimeResponseDto(junctionBoxId, junctionBoxData);
    }

    private JunctionBoxDataRealtimeResponseDto createJunctionBoxDataRealtimeResponseDto(Long junctionBoxId, JunctionBoxData junctionBoxData) {
        Double todayPowerSum = this.sumTodayPower(junctionBoxId);
        return new JunctionBoxDataRealtimeResponseDto(junctionBoxId, junctionBoxData.getPower(), todayPowerSum);
    }

    private Double sumTodayPower(Long junctionBoxId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<JunctionBoxData> junctionBoxDataList = junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBoxId, start, end);

        return junctionBoxDataList.stream()
                .mapToDouble(JunctionBoxData::getPower)
                .sum();
    }


}
