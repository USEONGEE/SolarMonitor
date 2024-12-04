package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.JunctionBoxDataRequestDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.InverterType;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.repository.InverterRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import com.energy.outsourcing.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledDataService {
    private final InverterRepository inverterRepository;
    private final JunctionBoxRepository junctionBoxRepository;
    private final DataRequester dataRequester;
    private final DataProcessor dataProcessor;
    private final WeatherService weatherService;

    // 매분마다 데이터를 요청하고 저장하는 스케줄러 메서드
    @Scheduled(fixedRate = 60000) // 10초마다 실행
    public void fetchDataAndProcess() {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("Data fetching and processing started at {}", timestamp);

        // 모든 인버터를 조회하고 단상, 삼상 데이터 요청
        for (Inverter inverter : inverterRepository.findAll()) {

            switch (inverter.getInverterType()) {
                case SINGLE:
                    SinglePhaseInverterDto singlePhaseData = dataRequester.requestSinglePhaseData(inverter.getId());
                    dataProcessor.processSinglePhaseData(singlePhaseData, timestamp);
                    break;
                case THREE:
                    ThreePhaseInverterDto threePhaseData = dataRequester.requestThreePhaseData(inverter.getId());
                    dataProcessor.processThreePhaseData(threePhaseData, timestamp);
                    break;

                default:
                    log.error("Unknown inverter type: {}", inverter.getInverterType());
            }

        }

        // 모든 접속함을 조회하고 접속함별 데이터 요청
        for (JunctionBox junctionBox : junctionBoxRepository.findAll()) {
            Long junctionBoxId = junctionBox.getId();
            JunctionBoxDataRequestDto junctionBoxDataRequestDto = dataRequester.requestJunctionBoxData(junctionBoxId);
            dataProcessor.processJunctionBoxData(junctionBoxId, junctionBoxDataRequestDto, timestamp);
        }
    }
    // 1시간마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void fetchWeatherData() {
        weatherService.fetchAndSaveWeatherData();
    }



}
