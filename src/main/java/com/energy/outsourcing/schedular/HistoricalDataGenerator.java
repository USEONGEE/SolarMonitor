package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.*;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
import com.energy.outsourcing.repository.InverterRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile("test || dev")
@Slf4j
public class HistoricalDataGenerator implements ApplicationRunner {

    private final InverterRepository inverterRepository;
    private final InverterAccumulationRepository accumulationRepository;
    private final DataRequester dataRequester;
    private final DataProcessor dataProcessor;
    private final JunctionBoxRepository junctionBoxRepository;
    private final Map<Long, Double> inverterCumMonth = new HashMap<>();

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Generating historical data...");
        LocalDateTime startDateTime = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endDateTime = LocalDate.now().minusDays(1).atTime(23, 59);
        LocalDateTime currentDateTime = startDateTime;

        List<Inverter> inverters = inverterRepository.findAll();
        List<JunctionBox> junctionBoxes = junctionBoxRepository.findAll();


        if (inverters.isEmpty()) {
            log.warn("No inverters found. Historical data generation will be skipped.");
            return;
        }
        // 초기 누적 에너지 설정
        for (Inverter inverter : inverters) {
            inverterCumMonth.put(inverter.getId(), 0.0);
        }
        while (!currentDateTime.isAfter(endDateTime)) {
            // 인버터 데이터 저장
            for (Inverter inverter : inverters) {
                // MockDataRequester를 통해 데이터 요청
                InverterType type = inverter.getInverterType();
                InverterData inverterData = null;

                switch (type) {
                    case SINGLE:
                        SinglePhaseInverterDto dto = dataRequester.requestSinglePhaseData(inverter.getId());
                        inverterData = dataProcessor.processSinglePhaseData(dto, currentDateTime);
                        break;
                    case THREE:
                        ThreePhaseInverterDto dto2 = dataRequester.requestThreePhaseData(inverter.getId());
                        inverterData = dataProcessor.processThreePhaseData(dto2, currentDateTime);
                        break;
                    default:
                        throw new RuntimeException("Unknown inverter type: " + type);
                }

                // 매 59분 마다
                if (currentDateTime.toLocalTime().getMinute() == 59) {
                    InverterAccumulation hourlyAccumulation = createHourlyAccumulation(inverter, currentDateTime, inverterData.getCumulativeEnergy());
                    accumulationRepository.save(hourlyAccumulation);
                }

                // 일별 누적 데이터 저장 (자정 이후 하루가 끝나는 시점)
                if (currentDateTime.toLocalTime().equals(LocalTime.of(23, 59))) {
                    InverterAccumulation dailyAccumulation = createDailyAccumulation(inverter, currentDateTime, inverterData.getCumulativeEnergy());
                    accumulationRepository.save(dailyAccumulation);

                    // 자정마다 월별 데이터에 저장
                    Double v = inverterCumMonth.get(inverter.getId());
                    inverterCumMonth.put(inverter.getId(), v + inverterData.getCumulativeEnergy());
                }

                // 월별 누적 데이터 저장 (월의 마지막 날)
                if (currentDateTime.getDayOfMonth() == currentDateTime.toLocalDate().lengthOfMonth()
                        && currentDateTime.toLocalTime().equals(LocalTime.of(23, 59))) {
                    InverterAccumulation monthlyAccumulation = createMonthlyAccumulation(inverter, currentDateTime);
                    accumulationRepository.save(monthlyAccumulation);
                }
            }

            // 접점박스 데이터 저장
            for (JunctionBox junctionBox : junctionBoxes) {
                List<JunctionBoxChannelDataDto> junctionBoxDataList = dataRequester.requestJunctionBoxData(junctionBox.getId());
                dataProcessor.processJunctionBoxData(junctionBox.getId(), junctionBoxDataList, currentDateTime);
            }

            currentDateTime = currentDateTime.plusMinutes(1);
        }
        log.info("Historical data generation completed.");
    }
    private InverterAccumulation createHourlyAccumulation(Inverter inverter, LocalDateTime dateTime, double cumulativeEnergy) {
        InverterAccumulation hourlyAccumulation = new InverterAccumulation();
        hourlyAccumulation.setInverter(inverter);
        hourlyAccumulation.setCumulativeEnergy(cumulativeEnergy);
        hourlyAccumulation.setDate(dateTime);
        hourlyAccumulation.setType(AccumulationType.HOURLY);
        return hourlyAccumulation;
    }

    private InverterAccumulation createDailyAccumulation(Inverter inverter, LocalDateTime date, double cumulativeEnergy) {
        InverterAccumulation dailyAccumulation = new InverterAccumulation();
        dailyAccumulation.setInverter(inverter);
        dailyAccumulation.setCumulativeEnergy(cumulativeEnergy);
        dailyAccumulation.setDate(date);
        dailyAccumulation.setType(AccumulationType.DAILY);
        return dailyAccumulation;
    }

    private InverterAccumulation createMonthlyAccumulation(Inverter inverter, LocalDateTime date) {
        InverterAccumulation dailyAccumulation = new InverterAccumulation();
        dailyAccumulation.setInverter(inverter);
        Double cumulativeEnergy = inverterCumMonth.get(inverter.getId());
        dailyAccumulation.setCumulativeEnergy(cumulativeEnergy);
        dailyAccumulation.setDate(date);
        dailyAccumulation.setType(AccumulationType.MONTHLY);

        // 월별 누적 에너지 초기화
        inverterCumMonth.put(inverter.getId(), 0.0);
        return dailyAccumulation;
    }
}
