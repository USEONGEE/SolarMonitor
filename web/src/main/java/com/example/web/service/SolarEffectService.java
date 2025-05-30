package com.example.web.service;

import com.example.web.dto.SolarEffectDTO;
import com.example.web.entity.AccumulationType;
import com.example.web.entity.Inverter;
import com.example.web.entity.InverterAccumulation;
import com.example.web.repository.InverterAccumulationRepository;
import com.example.web.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolarEffectService {
    private static final double CO2_EMISSION_FACTOR = 0.4594; // tCO2/kWh
    private static final double TREE_CO2_ABSORPTION_PER_YEAR = 17.5; // kg/year
    private static final double OIL_CONVERSION_FACTOR = 0.213; // L/kWh

    private final InverterAccumulationRepository accumulationRepository;
    private final InverterRepository inverterRepository;



    // CO2 절감량 계산
    public double calculateCO2Reduction(double generation) {
        //return generation * (CO2_EMISSION_FACTOR / 1000000); // Convert MWh to Wh
        return (generation / 1000) * CO2_EMISSION_FACTOR ; // Convert wh to kWh
    }

    // 나무 심기 효과 계산
    public double calculateTreePlantingEffect(double co2Reduction) {
        return co2Reduction / TREE_CO2_ABSORPTION_PER_YEAR;
    }

    // 석유 절감량 계산
    public double calculateOilSaving(double generation) {
        return (generation / 1000) * OIL_CONVERSION_FACTOR;
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


    // 누적 발전량 계산
    // public API
    public double getTotalAccumulationUntilLastMonth() {
        LocalDateTime start = LocalDate.now()
                .withDayOfYear(1)
                .atStartOfDay();
        LocalDateTime end = LocalDate.now()
                .atTime(23, 59, 59, 999_999_999);

        return calculateDailyAccumulation(start, end);
    }

    public double getThisMonthAccumulation() {
        LocalDateTime start = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime end = LocalDate.now()
                .atTime(23, 59, 59, 999_999_999);

        return calculateDailyAccumulation(start, end);
    }

    // 공통 로직을 뽑아낸 private 메서드
    private double calculateDailyAccumulation(LocalDateTime start, LocalDateTime end) {
        List<Inverter> inverters = inverterRepository.findAll();
        double total = 0.0;

        for (Inverter inv : inverters) {
            List<InverterAccumulation> list = accumulationRepository
                    .findByInverterIdAndTypeAndDateBetween(
                            inv.getId(),
                            AccumulationType.DAILY,
                            start,
                            end
                    );

            if (list.isEmpty()) continue;

            double max = list.stream()
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .max().orElse(0.0);
            double min = list.stream()
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .min().orElse(0.0);

            total += (max - min);
        }
        log.info("Total accumulation this month: {}", total);
        return total;
    }
}
