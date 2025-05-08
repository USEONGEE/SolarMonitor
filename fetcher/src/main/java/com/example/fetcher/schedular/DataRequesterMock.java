package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Profile("test")
public class DataRequesterMock implements DataRequester {

    private final Random random = new Random();

    @Override
    public SinglePhaseInverterDto requestSinglePhaseData(Long inverterId) {
        return new SinglePhaseInverterDto(
                295.0 + (random.nextDouble() * 10), // PV 전압 295V ~ 305V
                random.nextDouble() * 20,           // PV 전류 0 ~ 20A
                random.nextDouble() * 5000,         // PV 출력 0 ~ 5000W
                220.0 + (random.nextDouble() * 10), // 계통 전압 220V ~ 230V
                random.nextDouble() * 20,           // 계통 전류 0 ~ 20A
                random.nextDouble() * 5000,         // 현재 출력 0 ~ 5000W
                0.9 + (random.nextDouble() * 0.2),  // 역률 0.9 ~ 1.1
                49.5 + (random.nextDouble()),        // 주파수 49.5Hz ~ 50.5Hz
                random.nextDouble() * 10000,         // 누적 발전량 0 ~ 10000Wh
                random.nextInt(2)                     // 고장 여부 (0: 정상, 1: 고장)
        );
    }

    @Override
    public ThreePhaseInverterDto requestThreePhaseData(Long inverterId) {
        return new ThreePhaseInverterDto(
                295.0 + (random.nextDouble() * 10),   // PV 전압 (평균) 295V ~ 305V
                random.nextDouble() * 20,             // PV 전류 (합) 0 ~ 20A
                random.nextDouble() * 5000,           // PV 출력 0 ~ 5000W
                395.0 + (random.nextDouble() * 10),   // RS선간 전압 395V ~ 405V
                395.0 + (random.nextDouble() * 10),   // ST선간 전압 395V ~ 405V
                395.0 + (random.nextDouble() * 10),   // TR선간 전압 395V ~ 405V
                random.nextDouble() * 20,             // R상 전류 0 ~ 20A
                random.nextDouble() * 20,             // S상 전류 0 ~ 20A
                random.nextDouble() * 20,             // T상 전류 0 ~ 20A
                random.nextDouble() * 5000,           // 현재 출력 0 ~ 5000W
                0.9 + (random.nextDouble() * 0.2),    // 역률 0.9 ~ 1.1
                49.5 + (random.nextDouble()),          // 주파수 49.5Hz ~ 50.5Hz
                random.nextDouble() * 10000,           // 누적 발전량 0 ~ 10000Wh
                random.nextInt(2)                       // 고장 여부 (0: 정상, 1: 고장)
        );
    }

    @Override
    public JunctionBoxDataRequestDto requestJunctionBoxData(Long inverterId) {
        return new JunctionBoxDataRequestDto(295.0 + (random.nextDouble() * 10),
                random.nextDouble() * 10);
    }

    @Override
    public SeasonalPanelDataDto requestSeasonal(String port, Long inverterId) {
        return new SeasonalPanelDataDto(
                random.nextDouble() * 1000, // 경사일사량
                random.nextDouble() * 50,   // 외기온도
                random.nextDouble() * 1000, // 수평일사량
                random.nextDouble() * 50,    // 모듈온도
                inverterId
        );
    }


}
