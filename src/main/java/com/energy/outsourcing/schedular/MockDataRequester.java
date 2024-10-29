package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MockDataRequester implements DataRequester {

    private final Random random = new Random();

    @Override
    public SinglePhaseInverterDto requestSinglePhaseData(Long inverterId) {
        return new SinglePhaseInverterDto(
                random.nextDouble() * 1000, // PV 전압
                random.nextDouble() * 20,   // PV 전류
                random.nextDouble() * 5000, // PV 출력
                random.nextDouble() * 500,  // 계통 전압
                random.nextDouble() * 20,   // 계통 전류
                random.nextDouble() * 5000, // 현재 출력
                random.nextDouble(),        // 역률
                random.nextDouble() * 60,   // 주파수
                random.nextDouble() * 10000, // 누적 발전량
                random.nextInt(2)           // 고장 여부
        );
    }

    @Override
    public ThreePhaseInverterDto requestThreePhaseData(Long inverterId) {
        return new ThreePhaseInverterDto(
                random.nextDouble() * 1000,  // PV 전압 (평균)
                random.nextDouble() * 20,    // PV 전류 (합)
                random.nextDouble() * 5000,  // PV 출력
                random.nextDouble() * 500,   // RS선간 전압
                random.nextDouble() * 500,   // ST선간 전압
                random.nextDouble() * 500,   // TR선간 전압
                random.nextDouble() * 20,    // R상 전류
                random.nextDouble() * 20,    // S상 전류
                random.nextDouble() * 20,    // T상 전류
                random.nextDouble() * 5000,  // 현재 출력
                random.nextDouble(),         // 역률
                random.nextDouble() * 60,    // 주파수
                random.nextDouble() * 10000, // 누적 발전량
                random.nextInt(2)            // 고장 여부
        );
    }

    @Override
    public List<JunctionBoxChannelDataDto> requestJunctionBoxData(Long junctionBoxId) {
        List<JunctionBoxChannelDataDto> channelDataList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            channelDataList.add(new JunctionBoxChannelDataDto(
                    i,                          // 채널 번호
                    random.nextInt(10000),      // 원시 전압 데이터
                    random.nextInt(4000) - 2000 // 원시 전류 데이터
            ));
        }
        return channelDataList;
    }
}
