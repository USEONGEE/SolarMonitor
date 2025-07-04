package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import com.example.web.entity.Inverter;
import com.example.web.entity.JunctionBox;
import com.example.web.repository.InverterRepository;
import com.example.web.repository.JunctionBoxRepository;
import com.example.web.service.Weather2Service;
import com.example.web.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class SchedulerProd {
    private final InverterRepository inverterRepository;
    private final JunctionBoxRepository junctionBoxRepository;
    private final DataRequester dataRequester;
    private final DataProcessor dataProcessor;
    private final WeatherService weatherService;
    private final Weather2Service weather2Service;

    @Scheduled(fixedRate = 60000)
    public void fetchDataAndProcessProd() {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("fetch prod");
        log.info("Data fetching and processing started at {}", timestamp);

        // 모든 인버터를 조회하고 단상, 삼상 데이터 요청
        for (Inverter inverter : inverterRepository.findAll()) {
            try {
                switch (inverter.getInverterType()) {
                    case SINGLE:
                        dataRequester.requestSinglePhaseData(inverter.getId());
                        break;
                    case THREE:
                        dataRequester.requestThreePhaseData(inverter.getId());
                        break;

                    default:
                        log.error("Unknown inverter type: {}", inverter.getInverterType());
                }

                dataRequester.requestJunctionBoxData(inverter.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SeasonalPanelDataDto seasonalPanelDataDto = dataRequester.requestSeasonal("COM10", 1L);
        dataProcessor.processSeasonalPanelData(seasonalPanelDataDto);
        log.info("seasonalPanelDataDto: {}", seasonalPanelDataDto);
        SeasonalPanelDataDto seasonalPanelDataDto1 = dataRequester.requestSeasonal("COM11", 2L);
        dataProcessor.processSeasonalPanelData(seasonalPanelDataDto1);
        log.info("seasonalPanelDataDto: {}", seasonalPanelDataDto1);
    }


    // 1시간마다 실행
    @Scheduled(cron = "0 0 * * * *")
    public void fetchWeatherData() {
        weatherService.fetchAndSaveWeatherData();

        LocalDateTime now = LocalDateTime.now().minusHours(1); // 한 시간 전 기준
        String tm = now.format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmm"));
        weather2Service.fetchAndSave(tm);
    }

}
