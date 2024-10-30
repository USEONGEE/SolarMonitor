package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.entity.*;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
import com.energy.outsourcing.repository.InverterDataRepository;
import com.energy.outsourcing.repository.InverterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
public class HistoricalDataGeneratorService implements ApplicationRunner {

    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;
    private final DataRequester dataRequester;

    private Map<Long, Double> inverterCumulativeEnergyMap = new HashMap<>();

    // 일별 변동 계수를 저장할 변수
    private double dailyVariationFactor = 1.0;
    private LocalDate currentProcessingDate = null;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Generating historical data...");
        LocalDateTime startDateTime = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endDateTime = LocalDate.now().minusDays(1).atTime(23, 59);
        LocalDateTime currentDateTime = startDateTime;

        List<Inverter> inverters = inverterRepository.findAll();

        if (inverters.isEmpty()) {
            log.warn("No inverters found. Historical data generation will be skipped.");
            return;
        }

        // 초기 누적 에너지 설정
        for (Inverter inverter : inverters) {
            inverterCumulativeEnergyMap.put(inverter.getId(), 0.0);
        }

        while (!currentDateTime.isAfter(endDateTime)) {
            log.debug("Processing timestamp: {}", currentDateTime);
            LocalDate date = currentDateTime.toLocalDate();

            // 새로운 날이 시작되면 변동 계수를 생성
            if (!date.equals(currentProcessingDate)) {
                currentProcessingDate = date;
                dailyVariationFactor = 0.95 + (0.10 * Math.random()); // 0.95 ~ 1.05
                log.debug("New day detected: {}. Variation factor set to {}", date, dailyVariationFactor);
            }

            for (Inverter inverter : inverters) {

                // 자정에 누적 에너지 초기화
                if (currentDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                    inverterCumulativeEnergyMap.put(inverter.getId(), 0.0);
                    log.debug("Cumulative energy reset for inverter ID: {}", inverter.getId());
                }

                // 현재 누적 에너지 가져오기
                Double cumulativeEnergy = inverterCumulativeEnergyMap.getOrDefault(inverter.getId(), 0.0);

                // MockDataRequester를 통해 데이터 요청
                InverterType type = inverter.getInverterType();
                InverterData inverterData = null;

                if (type == InverterType.SINGLE) {
                    SinglePhaseInverterDto dto = dataRequester.requestSinglePhaseData(inverter.getId());
                    inverterData = SinglePhaseInverterData.fromDTO(dto);
                } else if (type == InverterType.THREE) {
                    ThreePhaseInverterDto dto = dataRequester.requestThreePhaseData(inverter.getId());
                    inverterData = ThreePhaseInverterData.fromDTO(dto);
                } else {
                    log.error("Unknown InverterType: {}", type);
                    continue; // 다음 인버터로 이동
                }

                // 공통 필드 설정
                inverterData.setInverter(inverter);
                inverterData.setTimestamp(currentDateTime);
                inverterData.setCumulativeEnergy(cumulativeEnergy);
                inverterData.setCurrentOutput(inverterData.getCurrentOutput());
                inverterData.setPowerFactor(inverterData.getPowerFactor());
                inverterData.setFrequency(inverterData.getFrequency());
                inverterData.setFaultStatus(inverterData.getFaultStatus());

                // 누적 에너지 업데이트
                double energyProduced = inverterData.getCurrentOutput() / 60.0; // W * (1/60)h = Wh
                cumulativeEnergy += energyProduced;
                inverterCumulativeEnergyMap.put(inverter.getId(), cumulativeEnergy);
                inverterData.setCumulativeEnergy(cumulativeEnergy);

                // 개별적으로 저장
                inverterDataRepository.save(inverterData);
                log.debug("Saved InverterData for inverter ID: {}, timestamp: {}", inverter.getId(), currentDateTime);

                // 시간별 누적 데이터 저장 (매 시간 끝에)
                if (currentDateTime.toLocalTime().getMinute() == 59) {
                    InverterAccumulation hourlyAccumulation = createHourlyAccumulation(inverter, currentDateTime, cumulativeEnergy);
                    accumulationRepository.save(hourlyAccumulation);
                    log.debug("Saved Hourly Accumulation for inverter ID: {}, datetime: {}, energy: {}", inverter.getId(), currentDateTime, cumulativeEnergy);
                }

                // 일별 누적 데이터 저장 (자정 이후 하루가 끝나는 시점)
                if (currentDateTime.toLocalTime().equals(LocalTime.of(23, 59))) {
                    InverterAccumulation dailyAccumulation = createDailyAccumulation(inverter, currentDateTime, cumulativeEnergy);
                    accumulationRepository.save(dailyAccumulation);
                    log.debug("Saved Daily Accumulation for inverter ID: {}, date: {}, energy: {}", inverter.getId(), date.minusDays(1), cumulativeEnergy);
                }
            }

            // 월별 누적 데이터 저장 (월의 마지막 날)
            if (currentDateTime.getDayOfMonth() == currentDateTime.toLocalDate().lengthOfMonth()
                    && currentDateTime.toLocalTime().equals(LocalTime.of(23, 59))) {
                saveMonthlyAccumulation(currentDateTime.toLocalDate());
            }

            // 시간 1분 증가
            currentDateTime = currentDateTime.plusMinutes(1);
        }

        log.info("Historical data generation completed.");
    }

    /**
     * Creates an hourly accumulation instance.
     *
     * @param inverter         The inverter entity.
     * @param dateTime         The date and time of accumulation.
     * @param cumulativeEnergy The cumulative energy for the hour.
     * @return An InverterAccumulation instance.
     */
    private InverterAccumulation createHourlyAccumulation(Inverter inverter, LocalDateTime dateTime, double cumulativeEnergy) {
        InverterAccumulation hourlyAccumulation = new InverterAccumulation();
        hourlyAccumulation.setInverter(inverter);
        hourlyAccumulation.setCumulativeEnergy(cumulativeEnergy);
        hourlyAccumulation.setDate(dateTime);
        hourlyAccumulation.setType(AccumulationType.HOURLY);
        return hourlyAccumulation;
    }

    /**
     * Creates a daily accumulation instance.
     *
     * @param inverter         The inverter entity.
     * @param date             The date of accumulation.
     * @param cumulativeEnergy The cumulative energy for the day.
     * @return An InverterAccumulation instance.
     */
    private InverterAccumulation createDailyAccumulation(Inverter inverter, LocalDateTime date, double cumulativeEnergy) {
        InverterAccumulation dailyAccumulation = new InverterAccumulation();
        dailyAccumulation.setInverter(inverter);
        dailyAccumulation.setCumulativeEnergy(cumulativeEnergy);
        dailyAccumulation.setDate(date);
        dailyAccumulation.setType(AccumulationType.DAILY);
        return dailyAccumulation;
    }

    /**
     * Saves monthly accumulation data by summing daily accumulations.
     *
     * @param date The last date of the month.
     */
    @Transactional
    public void saveMonthlyAccumulation(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date;

        // LocalDateTime으로 변환
        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(23, 59, 59, 999999999);

        List<Inverter> inverters = inverterRepository.findAll();

        for (Inverter inverter : inverters) {
            List<InverterAccumulation> dailyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                    inverter.getId(),
                    AccumulationType.DAILY,
                    startDateTime,
                    endDateTime
            );

            double monthlyCumulativeEnergy = dailyAccumulations.stream()
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();

            InverterAccumulation monthlyAccumulation = new InverterAccumulation();
            monthlyAccumulation.setInverter(inverter);
            monthlyAccumulation.setCumulativeEnergy(monthlyCumulativeEnergy);

            // 해당 월의 마지막 날로 저장
            LocalDateTime lastDayOfMonthDateTime = lastDayOfMonth.atTime(23, 59);
            monthlyAccumulation.setDate(lastDayOfMonthDateTime);

            monthlyAccumulation.setType(AccumulationType.MONTHLY);

            accumulationRepository.save(monthlyAccumulation);
            log.debug("Saved Monthly Accumulation for inverter ID: {}, date: {}, energy: {}",
                    inverter.getId(), lastDayOfMonthDateTime.toLocalDate(), monthlyCumulativeEnergy);
        }
    }
}
