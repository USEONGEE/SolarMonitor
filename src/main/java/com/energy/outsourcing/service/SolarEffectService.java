package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.SolarEffectDTO;
import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.InverterAccumulation;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
import com.energy.outsourcing.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolarEffectService {
    private static final double CO2_EMISSION_FACTOR = 0.4594; // tCO2/MWh
    private static final double TREE_CO2_ABSORPTION_PER_YEAR = 17.5; // kg/year
    private static final double OIL_CONVERSION_FACTOR = 0.213; // L/kWh

    private final InverterAccumulationRepository accumulationRepository;
    private final InverterRepository inverterRepository;


    // 누적 발전량 계산
    public double getTotalAccumulationUntilLastMonth() {
        LocalDateTime startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfLastMonth = LocalDate.now().withDayOfMonth(1).minusDays(1).atTime(23, 59, 59, 999999999);

        List<Inverter> inverters = inverterRepository.findAll();

        double sum = 0;
        for (Inverter inverter : inverters) {
            List<InverterAccumulation> yearlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                    inverter.getId(),
                    AccumulationType.MONTHLY,
                    startOfYear,
                    endOfLastMonth
            );

            sum += yearlyAccumulations.stream()
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();
        }
        return sum;
    }

    // CO2 절감량 계산
    public double calculateCO2Reduction(double generation) {
        return generation * (CO2_EMISSION_FACTOR * 1000); // Convert MWh to kWh
    }

    // 나무 심기 효과 계산
    public double calculateTreePlantingEffect(double co2Reduction) {
        return co2Reduction / TREE_CO2_ABSORPTION_PER_YEAR;
    }

    // 석유 절감량 계산
    public double calculateOilSaving(double generation) {
        return generation * OIL_CONVERSION_FACTOR;
    }

    // 전체 프로세스 실행 및 DTO 반환
    public SolarEffectDTO calculateEffects() {
        double thisMonthGeneration = getThisMonthAccumulation();
        double totalGenerationUntilLastMonth = getTotalAccumulationUntilLastMonth();

        double co2ReductionUntilLastMonth = calculateCO2Reduction(totalGenerationUntilLastMonth);
        double co2ReductionThisMonth = calculateCO2Reduction(thisMonthGeneration);

        double treePlantingEffectUntilLastMonth = calculateTreePlantingEffect(co2ReductionUntilLastMonth);
        double treePlantingEffectThisMonth = calculateTreePlantingEffect(co2ReductionThisMonth);

        double oilSavingUntilLastMonth = calculateOilSaving(totalGenerationUntilLastMonth);
        double oilSavingThisMonth = calculateOilSaving(thisMonthGeneration);

        return new SolarEffectDTO(
                totalGenerationUntilLastMonth,
                thisMonthGeneration,
                co2ReductionUntilLastMonth,
                co2ReductionThisMonth,
                treePlantingEffectUntilLastMonth,
                treePlantingEffectThisMonth,
                oilSavingUntilLastMonth,
                oilSavingThisMonth
        );
    }

    // 이번 달 누적 발전량 조회
    public Double getThisMonthAccumulation() {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59, 999999999);

        List<Inverter> inverters = inverterRepository.findAll();

        double sum = 0;
        for (Inverter inverter : inverters) {
            List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                    inverter.getId(),
                    AccumulationType.DAILY,
                    startDateTime,
                    endDateTime
            );

            sum += monthlyAccumulations.stream()
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();
        }

        return sum;
    }
}
