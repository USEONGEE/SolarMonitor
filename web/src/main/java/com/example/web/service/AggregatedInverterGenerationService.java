package com.example.web.service;

import com.example.web.dto.AggregatedInverterGenerationDto;
import com.example.web.entity.AccumulationType;
import com.example.web.entity.Inverter;
import com.example.web.entity.InverterAccumulation;
import com.example.web.entity.InverterData;
import com.example.web.repository.InverterAccumulationRepository;
import com.example.web.repository.InverterDataRepository;
import com.example.web.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregatedInverterGenerationService {

    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;

    /**
     * 전체 인버터의 금일, 전일, 금월, 전월, 연간, 전년, 누적 발전량과 전체 현재 출력 합계를 계산하여 반환합니다.
     *
     * @return AggregatedInverterGenerationDto
     */
    @Transactional(readOnly = true)
    public AggregatedInverterGenerationDto getAggregatedGeneration() {
        Double todayGeneration = calculateTodayGeneration();
        Double previousDayGeneration = calculatePreviousDayGeneration();
        Double monthlyGeneration = calculateMonthlyGeneration();
        Double previousMonthGeneration = calculatePreviousMonthGeneration();
        Double yearlyGeneration = calculateYearlyGeneration();
        Double previousYearGeneration = calculatePreviousYearGeneration();
        Double cumulativeGeneration = calculateCumulativeGeneration();
        Double totalCurrentOutput = calculateTotalCurrentOutput();

        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);


        return new AggregatedInverterGenerationDto(
                todayGeneration,
                previousDayGeneration,
                monthlyGeneration,
                previousMonthGeneration,
                yearlyGeneration,
                previousYearGeneration,
                cumulativeGeneration,
                totalCurrentOutput,
                calculativeGenerationTime(yesterday),
                calculativeTodayGenerationTime()
        );
    }

    private long calculativeTodayGenerationTime() {
        List<Inverter> inverters = inverterRepository.findAll();
        TimeUtils.LocalDateTimeRange dayRangeBy = TimeUtils.getDayRangeBy(LocalDate.now());
        long cumulativeGenerationTime = 0;
        for (Inverter inverter : inverters) {
            Long time = inverterDataRepository.countByInverterIdAndTimestampBetween(
                    inverter.getId(),
                    dayRangeBy.getStart(),
                    dayRangeBy.getEnd()
            );
            cumulativeGenerationTime += time;
        }
        return cumulativeGenerationTime / inverters.size();
    }

    private long calculativeGenerationTime(LocalDate localDate) {
        List<Inverter> inverters = inverterRepository.findAll();
        TimeUtils.LocalDateTimeRange dayRangeBy = TimeUtils.getDayRangeBy(localDate);
        long cumulativeGenerationTime = 0;
        for (Inverter inverter : inverters) {
            List<InverterAccumulation> byInverterIdAndTypeAndDateBetween = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                    inverter.getId(),
                    AccumulationType.DAILY,
                    dayRangeBy.getStart(),
                    dayRangeBy.getEnd()
            );

            if (byInverterIdAndTypeAndDateBetween.isEmpty()) {
                continue;
            }
            InverterAccumulation inverterAccumulation = byInverterIdAndTypeAndDateBetween.get(0);
            cumulativeGenerationTime += inverterAccumulation.getGenerationTime();
        }

        return cumulativeGenerationTime / inverters.size();
    }

    /**
     * 금일 전체 인버터의 발전량을 계산합니다.
     *
     * @return 금일 발전량 [Wh]
     */
    private Double calculateTodayGeneration() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(23, 59, 59);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalTodayGeneration = 0.0;

        for (Inverter inverter : inverters) {
            // 각 인버터의 금일 최신 데이터 조회
            Optional<InverterData> latestDataOpt = inverterDataRepository.findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                    inverter.getId(),
                    startOfToday,
                    endOfToday
            );

            if (latestDataOpt.isPresent()) {
                InverterData latestData = latestDataOpt.get();
                totalTodayGeneration += latestData.getCumulativeEnergy();
            }
        }

        return totalTodayGeneration;
    }

    /**
     * 전일 전체 인버터의 발전량을 계산합니다.
     *
     * @return 전일 발전량 [Wh]
     */
    private Double calculatePreviousDayGeneration() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(23, 59, 59);

        List<InverterAccumulation> dailyAccumulations = accumulationRepository.findByTypeAndDateBetween(
                AccumulationType.DAILY,
                startOfYesterday,
                endOfYesterday
        );

        return dailyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    /**
     * 금월 전체 인버터의 발전량을 계산합니다.
     *
     * @return 금월 발전량 [Wh]
     */
    private Double calculateMonthlyGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);

        List<InverterAccumulation> dailyAccumulations = accumulationRepository.findByTypeAndDateBetween(
                AccumulationType.DAILY,
                startDateTime,
                endDateTime
        );

        return dailyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    /**
     * 전월 전체 인버터의 발전량을 계산합니다.
     *
     * @return 전월 발전량 [Wh]
     */
    private Double calculatePreviousMonthGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfPreviousMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
        LocalDateTime startDateTime = firstDayOfPreviousMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfPreviousMonth.atTime(23, 59, 59);

        List<InverterAccumulation> dailyAccumulations = accumulationRepository.findByTypeAndDateBetween(
                AccumulationType.DAILY,
                startDateTime,
                endDateTime
        );

        return dailyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    /**
     * 연간 전체 인버터의 발전량을 계산합니다.
     *
     * @return 연간 발전량 [Wh]
     */
    private Double calculateYearlyGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = today.withDayOfYear(1);
        LocalDateTime startDateTime = firstDayOfYear.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);

        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByTypeAndDateBetween(
                AccumulationType.MONTHLY,
                startDateTime,
                endDateTime
        );

        return monthlyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    /**
     * 전년 전체 인버터의 발전량을 계산합니다.
     *
     * @return 전년 발전량 [Wh]
     */
    private Double calculatePreviousYearGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfPreviousYear = today.minusYears(1).withDayOfYear(1);
        LocalDate lastDayOfPreviousYear = today.minusYears(1).withDayOfYear(today.minusYears(1).lengthOfYear());
        LocalDateTime startDateTime = firstDayOfPreviousYear.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfPreviousYear.atTime(23, 59, 59);

        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByTypeAndDateBetween(
                AccumulationType.MONTHLY,
                startDateTime,
                endDateTime
        );

        return monthlyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    /**
     * 전체 누적 인버터의 발전량을 계산합니다.
     *
     * @return 누적 발전량 [Wh]
     */
    private Double calculateCumulativeGeneration() {
        List<InverterAccumulation> cumulativeAccumulations = accumulationRepository.findByType(AccumulationType.MONTHLY);

        return cumulativeAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    /**
     * 전체 인버터의 현재 출력 합계를 계산합니다.
     *
     * @return 전체 현재 출력 합계 [W]
     */
    private Double calculateTotalCurrentOutput() {
        log.info("Calculating total current output...");
        List<Inverter> inverters = inverterRepository.findAll();
        double totalCurrentOutput = 0.0;


        for (Inverter inverter : inverters) {
            log.info("Inverter ID: {}", inverter.getId());
            // 각 인버터의 최신 InverterData 조회
            Optional<InverterData> latestDataOpt = inverterDataRepository.findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                    inverter.getId(),
                    LocalDate.now().atStartOfDay(), // 오늘 날짜
                    LocalDateTime.now()   // 현재 시간
            );

            if(latestDataOpt.isPresent()) {
                InverterData latestData = latestDataOpt.get();
                totalCurrentOutput += latestData.getCurrentOutput();
                log.info("Current output: {}", latestData.getCurrentOutput());
            }
        }
        log.info("Total current output: {}", totalCurrentOutput);
        return totalCurrentOutput;
    }
}
