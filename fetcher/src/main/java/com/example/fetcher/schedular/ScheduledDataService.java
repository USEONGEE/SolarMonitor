package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import com.example.web.entity.Inverter;
import com.example.web.entity.JunctionBox;
import com.example.web.repository.InverterRepository;
import com.example.web.repository.JunctionBoxRepository;
import com.example.web.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    @ConditionalOnProperty(name = "scheduler.profile", havingValue = "test", matchIfMissing = true)
    public void fetchDataAndProcess() {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("fetch test");
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

        SeasonalPanelDataDto seasonalPanelDataDto = dataRequester.requestSeasonal();
        dataProcessor.processSeasonalPanelData(seasonalPanelDataDto);
    }


    @Scheduled(fixedRate = 60000) // 10초마다 실행
    @ConditionalOnProperty(name = "scheduler.profile", havingValue = "prod", matchIfMissing = true)
    public void fetchDataAndProcessProd() {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("fetch prod");
        log.info("Data fetching and processing started at {}", timestamp);

        // 모든 인버터를 조회하고 단상, 삼상 데이터 요청
//        for (Inverter inverter : inverterRepository.findAll()) {
//
//            switch (inverter.getInverterType()) {
//                case SINGLE:
//                    dataRequester.requestSinglePhaseData(inverter.getId());
//                    break;
//                case THREE:
//                    dataRequester.requestThreePhaseData(inverter.getId());
//                    break;
//
//                default:
//                    log.error("Unknown inverter type: {}", inverter.getInverterType());
//            }
//
//            dataRequester.requestJunctionBoxData(inverter.getId());
//        }

        SeasonalPanelDataDto seasonalPanelDataDto = dataRequester.requestSeasonal();
        log.info("seasonalPanelDataDto: {}", seasonalPanelDataDto);
        dataProcessor.processSeasonalPanelData(seasonalPanelDataDto);
    }


    // 1시간마다 실행
    @Scheduled(cron = "0 0 * * * *")
    public void fetchWeatherData() {
        weatherService.fetchAndSaveWeatherData();
    }

}
