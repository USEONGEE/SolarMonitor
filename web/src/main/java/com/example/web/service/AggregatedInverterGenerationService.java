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
     * 전체 인버터의 금일, 전일, 금월, 전월, 연간, 전년, 누적 발전량과
     * 전체 현재 출력 합계를 계산하여 반환합니다.
     */
    @Transactional(readOnly = true)
    public AggregatedInverterGenerationDto getAggregatedGeneration() {
        Double todayGeneration = calculateTodayGeneration();
        Double previousDayGeneration = calculatePreviousDayGeneration();
        Double monthlyGeneration = calculateMonthlyGeneration();            // 이번 달
        Double previousMonthGeneration = calculatePreviousMonthGeneration(); // 저번 달
        Double yearlyGeneration = calculateYearlyGeneration();              // 올해
        Double previousYearGeneration = calculatePreviousYearGeneration();   // 작년
        Double cumulativeGeneration = calculateCumulativeGeneration();      // 전체 누적
        Double totalCurrentOutput = calculateTotalCurrentOutput();          // 현재 출력 합계

        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        long yesterdayGenerationTimeAvg = calculateGenerationTimeForDate(yesterday);
        long todayGenerationTimeAvg = calculateGenerationTimeForDate(now);

        return new AggregatedInverterGenerationDto(
                todayGeneration,
                previousDayGeneration,
                monthlyGeneration,
                previousMonthGeneration,
                yearlyGeneration,
                previousYearGeneration,
                cumulativeGeneration,
                totalCurrentOutput,
                yesterdayGenerationTimeAvg,
                todayGenerationTimeAvg
        );
    }

    // -------------------------------------------------------------
    // 1) 금일 발전량 = (오늘 마지막 누적) - (어제 마지막 누적), 인버터별로 구해서 합산
    // -------------------------------------------------------------
    private Double calculateTodayGeneration() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = LocalDateTime.now();

        LocalDateTime endOfYesterday = today.minusDays(1).atTime(23, 59, 59);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalTodayGen = 0.0;

        for (Inverter inv : inverters) {
            // (a) 오늘 마지막 스냅샷 (InverterData)
            Optional<InverterData> latestTodayOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                            inv.getId(),
                            startOfToday,
                            endOfToday
                    );

            // (b) 어제 마지막 스냅샷 (InverterData)
            Optional<InverterData> latestYesterdayOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfYesterday
                    );

            if (latestTodayOpt.isPresent()) {
                double todayCum = latestTodayOpt.get().getCumulativeEnergy();
                double prevCum = latestYesterdayOpt.map(InverterData::getCumulativeEnergy).orElse(0.0);
                totalTodayGen += (todayCum - prevCum);
            }
            // 오늘 데이터가 없으면 계산에서 제외
        }

        return totalTodayGen;
    }

    // -------------------------------------------------------------
    // 2) 전일 발전량 = (어제 마지막 누적) - (그 전날 마지막 누적), 인버터별로 합산
    // -------------------------------------------------------------
    private Double calculatePreviousDayGeneration() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(23, 59, 59);

        LocalDate theDayBefore = yesterday.minusDays(1);
        LocalDateTime endOfTheDayBefore = theDayBefore.atTime(23, 59, 59);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalPrevDayGen = 0.0;

        for (Inverter inv : inverters) {
            // (a) 어제 마지막 스냅샷
            Optional<InverterData> latestYesterdayOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                            inv.getId(),
                            startOfYesterday,
                            endOfYesterday
                    );

            // (b) 그 전날 마지막 스냅샷
            Optional<InverterData> latestDayBeforeOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfTheDayBefore
                    );

            if (latestYesterdayOpt.isPresent()) {
                double yCum = latestYesterdayOpt.get().getCumulativeEnergy();
                double prevCum = latestDayBeforeOpt.map(InverterData::getCumulativeEnergy).orElse(0.0);
                totalPrevDayGen += (yCum - prevCum);
            }
        }

        return totalPrevDayGen;
    }

    // -------------------------------------------------------------
    // 3) 금월 발전량 = (오늘 마지막 누적) - (전월 마지막 누적), 인버터별 합산
    // -------------------------------------------------------------
    private Double calculateMonthlyGeneration() {
        LocalDate today = LocalDate.now();
        LocalDateTime endOfToday = LocalDateTime.now();

        LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
        LocalDateTime startOfThisMonth = firstDayOfThisMonth.atStartOfDay();
        // “전월 마지막 시각” = 이번 달 1일 00:00 이전 1초
        LocalDateTime endOfLastMonth = startOfThisMonth.minusSeconds(1);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalThisMonthGen = 0.0;

        for (Inverter inv : inverters) {
            // (1) 이달 마지막 누적(InverterData)
            Optional<InverterData> latestThisMonthOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfToday
                    );

            // (2) 전월 마지막 누적(InverterData)
            Optional<InverterData> latestLastMonthOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfLastMonth
                    );

            if (latestThisMonthOpt.isPresent()) {
                double thisCum = latestThisMonthOpt.get().getCumulativeEnergy();
                double prevCum = latestLastMonthOpt.map(InverterData::getCumulativeEnergy).orElse(0.0);
                totalThisMonthGen += (thisCum - prevCum);
            }
        }

        return totalThisMonthGen;
    }

    // -------------------------------------------------------------
    // 4) 전월 발전량 = (전월 마지막 누적) - (전전월 마지막 누적), 인버터별 합산
    // -------------------------------------------------------------
    private Double calculatePreviousMonthGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfPrevMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDateTime startOfPrevMonth = firstDayOfPrevMonth.atStartOfDay();
        // 전월 마지막 시각
        LocalDateTime endOfPrevMonth = firstDayOfPrevMonth
                .withDayOfMonth(firstDayOfPrevMonth.lengthOfMonth())
                .atTime(23, 59, 59);

        // 전전월 마지막 시각 = 전월 1일 00:00 이전 1초
        LocalDateTime endOfMonthBeforePrev = startOfPrevMonth.minusSeconds(1);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalPrevMonthGen = 0.0;

        for (Inverter inv : inverters) {
            // (1) 전월 마지막 누적
            Optional<InverterData> latestPrevMonthOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfPrevMonth
                    );

            // (2) 전전월 마지막 누적
            Optional<InverterData> latestBeforePrevOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfMonthBeforePrev
                    );

            if (latestPrevMonthOpt.isPresent()) {
                double prevMonthCum = latestPrevMonthOpt.get().getCumulativeEnergy();
                double beforePrevCum = latestBeforePrevOpt.map(InverterData::getCumulativeEnergy).orElse(0.0);
                totalPrevMonthGen += (prevMonthCum - beforePrevCum);
            }
        }

        return totalPrevMonthGen;
    }

    // -------------------------------------------------------------
    // 5) 금년 발전량 = (오늘 마지막 누적) - (지난해 12월 31일 마지막 누적)
    // -------------------------------------------------------------
    private Double calculateYearlyGeneration() {
        LocalDate today = LocalDate.now();
        LocalDateTime endOfToday = LocalDateTime.now();

        LocalDate firstDayOfYear = today.withDayOfYear(1);
        LocalDateTime startOfYear = firstDayOfYear.atStartOfDay();
        // 지난해 마지막 시각 = 올해 1월 1일 00:00 이전 1초
        LocalDateTime endOfLastYear = startOfYear.minusSeconds(1);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalYearGen = 0.0;

        for (Inverter inv : inverters) {
            // (1) 올해 마지막 누적
            Optional<InverterData> latestThisYearOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfToday
                    );

            // (2) 지난해 마지막 누적
            Optional<InverterData> latestLastYearOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfLastYear
                    );

            if (latestThisYearOpt.isPresent()) {
                double thisCum = latestThisYearOpt.get().getCumulativeEnergy();
                double prevCum = latestLastYearOpt.map(InverterData::getCumulativeEnergy).orElse(0.0);
                totalYearGen += (thisCum - prevCum);
            }
        }

        return totalYearGen;
    }

    // -------------------------------------------------------------
    // 6) 전년 발전량 = (지난해 12월 31일 누적) - (전전년 12월 31일 누적)
    // -------------------------------------------------------------
    private Double calculatePreviousYearGeneration() {
        LocalDate today = LocalDate.now();

        LocalDate firstDayOfThisYear = today.withDayOfYear(1);
        LocalDateTime startOfThisYear = firstDayOfThisYear.atStartOfDay();
        // 지난해 마지막 시각
        LocalDateTime endOfLastYear = startOfThisYear.minusSeconds(1);

        LocalDate firstDayOfLastYear = today.minusYears(1).withDayOfYear(1);
        LocalDateTime startOfLastYear = firstDayOfLastYear.atStartOfDay();
        // 전전년 마지막 시각
        LocalDateTime endOfYearBeforeLast = startOfLastYear.minusSeconds(1);

        List<Inverter> inverters = inverterRepository.findAll();
        double totalPrevYearGen = 0.0;

        for (Inverter inv : inverters) {
            Optional<InverterData> latestLastYearOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfLastYear
                    );

            Optional<InverterData> latestBeforeLastYearOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            endOfYearBeforeLast
                    );

            if (latestLastYearOpt.isPresent()) {
                double lastYearCum = latestLastYearOpt.get().getCumulativeEnergy();
                double beforeLastCum = latestBeforeLastYearOpt.map(InverterData::getCumulativeEnergy).orElse(0.0);
                totalPrevYearGen += (lastYearCum - beforeLastCum);
            }
        }

        return totalPrevYearGen;
    }

    // -------------------------------------------------------------
    // 7) 전체 누적 발전량 = 각 인버터별 “가장 최신 누적값” 합산
    // -------------------------------------------------------------
    private Double calculateCumulativeGeneration() {
        List<Inverter> inverters = inverterRepository.findAll();
        double totalCumulative = 0.0;

        for (Inverter inv : inverters) {
            Optional<InverterData> latestOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBeforeOrderByTimestampDesc(
                            inv.getId(),
                            LocalDateTime.now()
                    );
            if (latestOpt.isPresent()) {
                totalCumulative += latestOpt.get().getCumulativeEnergy();
            }
        }

        return totalCumulative;
    }

    // -------------------------------------------------------------
    // 8) 전체 현재 출력 합계 = 오늘(00:00~지금) 범위 내
    //    각 인버터의 최신 InverterData.currentOutput 합산
    // -------------------------------------------------------------
    private Double calculateTotalCurrentOutput() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Inverter> inverters = inverterRepository.findAll();
        double totalCurrentOutput = 0.0;

        for (Inverter inv : inverters) {
            Optional<InverterData> latestDataOpt = inverterDataRepository
                    .findTopByInverterIdAndTimestampBetweenOrderByTimestampDesc(
                            inv.getId(),
                            startOfToday,
                            now
                    );

            if (latestDataOpt.isPresent()) {
                totalCurrentOutput += latestDataOpt.get().getCurrentOutput();
            }
        }

        return totalCurrentOutput;
    }

    // -------------------------------------------------------------
    // 9) 발전 시간(분) 평균 계산: 주어진 날짜의 DAILY InverterAccumulation.generationTime 값을
    //    인버터별로 합산 후 평균
    // -------------------------------------------------------------
    private long calculateGenerationTimeForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Inverter> inverters = inverterRepository.findAll();
        long sumGenTime = 0;
        int count = 0;

        for (Inverter inv : inverters) {
            Optional<InverterAccumulation> accOpt = accumulationRepository
                    .findTopByInverterIdAndTypeAndDateBetweenOrderByDateDesc(
                            inv.getId(),
                            AccumulationType.DAILY,
                            startOfDay,
                            endOfDay
                    );

            if (accOpt.isPresent()) {
                Long genTime = accOpt.get().getGenerationTime();
                if (genTime != null) {
                    sumGenTime += genTime;
                    count++;
                }
            }
        }

        return (count > 0) ? (sumGenTime / count) : 0L;
    }
}
