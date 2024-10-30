package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.repository.JunctionBoxDataRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JunctionBoxDataService {

    private final JunctionBoxRepository junctionBoxRepository;
    private final JunctionBoxDataRepository junctionBoxDataRepository;

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
}
