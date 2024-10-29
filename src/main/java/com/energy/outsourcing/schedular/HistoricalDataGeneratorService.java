package com.energy.outsourcing.schedular;

import com.energy.outsourcing.entity.*;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
import com.energy.outsourcing.repository.InverterDataRepository;
import com.energy.outsourcing.repository.InverterRepository;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
@Profile("test")
@Slf4j
public class HistoricalDataGeneratorService implements ApplicationRunner {

    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;

    private Map<Long, Double> inverterCumulativeEnergyMap = new HashMap<>();

    // 일별 변동 계수를 저장할 변수
    private double dailyVariationFactor = 1.0;
    private LocalDate currentProcessingDate = null;

    private final Random random = new Random();

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

        while (!currentDateTime.isAfter(endDateTime)) {
            log.debug("Processing timestamp: {}", currentDateTime);
            LocalDate date = currentDateTime.toLocalDate();

            // 새로운 날이 시작되면 변동 계수를 생성
            if (!date.equals(currentProcessingDate)) {
                currentProcessingDate = date;
                dailyVariationFactor = 0.95 + (0.10 * random.nextDouble()); // 0.95 ~ 1.05
                log.debug("New day detected: {}. Variation factor set to {}", date, dailyVariationFactor);
            }

            for (Inverter inverter : inverters) {

                // Reset cumulative energy at midnight
                if (currentDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                    inverterCumulativeEnergyMap.put(inverter.getId(), 0.0);
                    log.debug("Cumulative energy reset for inverter ID: {}", inverter.getId());
                }

                // Get current cumulative energy
                Double cumulativeEnergy = inverterCumulativeEnergyMap.getOrDefault(inverter.getId(), 0.0);

                // Simulate power output with daily variation
                double powerOutput = simulatePowerOutput(currentDateTime.toLocalTime(), dailyVariationFactor);

                // Energy produced in this minute (Wh)
                double energyProduced = powerOutput / 60.0; // Power (W) * time (h)

                // Update cumulative energy
                cumulativeEnergy += energyProduced;
                inverterCumulativeEnergyMap.put(inverter.getId(), cumulativeEnergy);

                // Create and save InverterData based on inverter type
                InverterData inverterData = createInverterData(inverter, currentDateTime, cumulativeEnergy, powerOutput);
                inverterDataRepository.save(inverterData);
                log.debug("Saved InverterData for inverter ID: {}, timestamp: {}", inverter.getId(), currentDateTime);

                // Save daily accumulation at the end of the day
                if (currentDateTime.toLocalTime().equals(LocalTime.of(23, 59))) {
                    saveDailyAccumulation(inverter, date, cumulativeEnergy);
                }
            }

            // Save monthly accumulation at the end of the month
            if (currentDateTime.getDayOfMonth() == currentDateTime.toLocalDate().lengthOfMonth()
                    && currentDateTime.toLocalTime().equals(LocalTime.of(23, 59))) {
                saveMonthlyAccumulation(currentDateTime.toLocalDate());
            }

            // Increment time by one minute
            currentDateTime = currentDateTime.plusMinutes(1);
        }
        log.info("Historical data generation completed.");
    }

    /**
     * Creates an instance of InverterData based on the inverter type.
     *
     * @param inverter          The inverter entity.
     * @param currentDateTime   The current timestamp.
     * @param cumulativeEnergy  The cumulative energy up to this timestamp.
     * @param powerOutput       The simulated power output.
     * @return An instance of SinglePhaseInverterData or ThreePhaseInverterData.
     */
    private InverterData createInverterData(Inverter inverter, LocalDateTime currentDateTime,
                                            double cumulativeEnergy, double powerOutput) {
        InverterData inverterData;

        if (inverter.getInverterType() == InverterType.SINGLE) {
            // Create SinglePhaseInverterData
            SinglePhaseInverterData singleData = new SinglePhaseInverterData();
            // Simulate additional fields for single-phase
            singleData.setGridVoltage(simulateGridVoltageSingle(currentDateTime.toLocalTime()));
            singleData.setGridCurrent(simulateGridCurrentSingle(powerOutput));

            inverterData = singleData;
        } else if (inverter.getInverterType() == InverterType.THREE) {
            // Create ThreePhaseInverterData
            ThreePhaseInverterData threePhaseData = new ThreePhaseInverterData();
            // Simulate additional fields for three-phase
            threePhaseData.setGridVoltageRS(simulateGridVoltageThree(currentDateTime.toLocalTime()));
            threePhaseData.setGridVoltageST(simulateGridVoltageThree(currentDateTime.toLocalTime()));
            threePhaseData.setGridVoltageTR(simulateGridVoltageThree(currentDateTime.toLocalTime()));
            threePhaseData.setGridCurrentR(simulateGridCurrentThree(powerOutput, "R"));
            threePhaseData.setGridCurrentS(simulateGridCurrentThree(powerOutput, "S"));
            threePhaseData.setGridCurrentT(simulateGridCurrentThree(powerOutput, "T"));

            inverterData = threePhaseData;
        } else {
            throw new IllegalArgumentException("Unknown InverterType: " + inverter.getInverterType());
        }

        // Set common fields
        inverterData.setInverter(inverter);
        inverterData.setTimestamp(currentDateTime);
        inverterData.setCumulativeEnergy(cumulativeEnergy);
        inverterData.setCurrentOutput(powerOutput);
        inverterData.setPowerFactor(0.95); // Example fixed value, adjust as needed
        inverterData.setFrequency(50.0); // Example fixed value, adjust as needed
        inverterData.setFaultStatus(0); // Example fixed value, adjust as needed

        return inverterData;
    }

    /**
     * Simulates grid voltage for single-phase inverters.
     *
     * @param time The current time.
     * @return Simulated grid voltage.
     */
    private double simulateGridVoltageSingle(LocalTime time) {
        // Simple simulation: voltage fluctuates around 230V with minor variations
        return 230.0 + (Math.random() * 5 - 2.5); // 227.5V to 232.5V
    }

    /**
     * Simulates grid current for single-phase inverters.
     *
     * @param powerOutput The simulated power output.
     * @return Simulated grid current.
     */
    private double simulateGridCurrentSingle(double powerOutput) {
        // Power (W) = Voltage (V) * Current (A) * Power Factor
        double voltage = 230.0; // Assume fixed voltage
        double powerFactor = 0.95; // Assume fixed power factor
        return powerOutput / (voltage * powerFactor);
    }

    /**
     * Simulates grid voltage for three-phase inverters.
     *
     * @param time The current time.
     * @return Simulated grid voltage for each phase.
     */
    private double simulateGridVoltageThree(LocalTime time) {
        // Simple simulation: voltage fluctuates around 400V with minor variations
        return 400.0 + (Math.random() * 10 - 5); // 395V to 405V
    }

    /**
     * Simulates grid current for three-phase inverters.
     *
     * @param powerOutput The simulated power output.
     * @param phase       The phase identifier (R, S, T).
     * @return Simulated grid current for the specified phase.
     */
    private double simulateGridCurrentThree(double powerOutput, String phase) {
        // Power (W) = √3 * Voltage (V) * Current (A) * Power Factor
        double voltage = 400.0; // Assume fixed voltage
        double powerFactor = 0.95; // Assume fixed power factor
        return powerOutput / (Math.sqrt(3) * voltage * powerFactor) / 3; // Distribute equally among phases
    }

    /**
     * Simulates power output based on the time of day and variation factor.
     *
     * @param time             The current time.
     * @param variationFactor  The daily variation factor.
     * @return Simulated power output in watts.
     */
    private double simulatePowerOutput(LocalTime time, double variationFactor) {
        int hour = time.getHour();
        int minute = time.getMinute();
        double totalMinutes = hour * 60 + minute;

        double sunrise = 6 * 60; // 6 AM
        double sunset = 18 * 60; // 6 PM
        double maxPowerTime = 12 * 60; // Noon
        double maxPower = 1000; // Max power output in W

        double basePower;
        if (totalMinutes < sunrise || totalMinutes > sunset) {
            basePower = 0.0;
        } else if (totalMinutes <= maxPowerTime) {
            // Increasing power output
            basePower = maxPower * (totalMinutes - sunrise) / (maxPowerTime - sunrise);
        } else {
            // Decreasing power output
            basePower = maxPower * (sunset - totalMinutes) / (sunset - maxPowerTime);
        }

        // Apply daily variation factor
        return basePower * variationFactor;
    }

    /**
     * Saves daily accumulation data.
     *
     * @param inverter         The inverter entity.
     * @param date             The date of accumulation.
     * @param cumulativeEnergy The cumulative energy for the day.
     */
    private void saveDailyAccumulation(Inverter inverter, LocalDate date, double cumulativeEnergy) {
        InverterAccumulation dailyAccumulation = new InverterAccumulation();
        dailyAccumulation.setInverter(inverter);
        dailyAccumulation.setCumulativeEnergy(cumulativeEnergy);
        dailyAccumulation.setDate(date);
        dailyAccumulation.setType(AccumulationType.DAILY);
        accumulationRepository.save(dailyAccumulation);
        log.debug("Saved Daily Accumulation for inverter ID: {}, date: {}, energy: {}", inverter.getId(), date, cumulativeEnergy);
    }

    /**
     * Saves monthly accumulation data by summing daily accumulations.
     *
     * @param date The last date of the month.
     */
    private void saveMonthlyAccumulation(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date;

        List<Inverter> inverters = inverterRepository.findAll();

        for (Inverter inverter : inverters) {
            List<InverterAccumulation> dailyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                    inverter.getId(),
                    AccumulationType.DAILY,
                    firstDayOfMonth,
                    lastDayOfMonth
            );

            double monthlyCumulativeEnergy = dailyAccumulations.stream()
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();

            InverterAccumulation monthlyAccumulation = new InverterAccumulation();
            monthlyAccumulation.setInverter(inverter);
            monthlyAccumulation.setCumulativeEnergy(monthlyCumulativeEnergy);
            monthlyAccumulation.setDate(firstDayOfMonth);
            monthlyAccumulation.setType(AccumulationType.MONTHLY);
            accumulationRepository.save(monthlyAccumulation);
            log.debug("Saved Monthly Accumulation for inverter ID: {}, month: {}, energy: {}", inverter.getId(), firstDayOfMonth, monthlyCumulativeEnergy);
        }
    }
}
