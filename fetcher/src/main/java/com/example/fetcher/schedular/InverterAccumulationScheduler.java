package com.example.fetcher.schedular;

import com.example.web.entity.*;
import com.example.web.repository.*;
import com.example.web.service.JunctionBoxDataAccumulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
// TODO 전체 예외처리 필요
public class InverterAccumulationScheduler {

    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;
    private final InverterRepository inverterRepository;
    private final JunctionBoxRepository junctionBoxRepository;
    private final JunctionBoxDataRepository junctionBoxDataRepository;
    private final JunctionBoxDataAccumulationService junctionBoxDataAccumulationService;

    // 매 시간마다 전 시간의 마지막 데이터를 저장
    @Scheduled(cron = "10 0 * * * ?") // 매 정각마다 실행
    @Transactional
    public void accumulateHourlyData() {
        log.info("Accumulate hourly data");

        List<Inverter> inverters = inverterRepository.findAll();

        TimeUtils.LocalDateTimeRange hourRangeBy = TimeUtils.getHourRangeBy(LocalDateTime.now().minusHours(1));
        LocalDateTime lastHourStart = hourRangeBy.getStart();
        LocalDateTime lastHourEnd = hourRangeBy.getEnd();

        for (Inverter inverter : inverters) {
            InverterData lastData = inverterDataRepository.findFirstByInverterIdAndTimestampLessThanOrderByTimestampDesc(inverter.getId(), lastHourStart) // TODO 여기에 문제
                    .orElse(null);
            if (lastData != null) {
                InverterAccumulation hourlyAccumulation = new InverterAccumulation(
                        inverter,
                        lastData.getCumulativeEnergy(),
                        AccumulationType.HOURLY,
                        lastHourEnd
                );

                accumulationRepository.save(hourlyAccumulation);
            }
        }
    }

    // 매일 자정에 전날 마지막 누적 발전량을 저장
    @Scheduled(cron = "10 0 0 * * ?")
    @Transactional
    public void accumulateDailyData() {
        log.info("Accumulate daily data");
        List<Inverter> inverters = inverterRepository.findAll();
        List<JunctionBox> junctionBoxes = junctionBoxRepository.findAll();

        TimeUtils.LocalDateTimeRange certainDayRange = TimeUtils.getDayRangeBy(
                LocalDate.now().minusDays(1));

        LocalDateTime yesterdayStart = certainDayRange.getStart();
        LocalDateTime yesterdayEnd = certainDayRange.getEnd();

        for (Inverter inverter : inverters) {
            // 어제 마지막 데이터 조회
            InverterData lastData = inverterDataRepository.findFirstByInverterIdAndTimestampLessThanOrderByTimestampDesc(inverter.getId(), yesterdayEnd)
                    .orElseThrow(() -> new RuntimeException("InverterData not found"));
            if (lastData == null) {
                continue;
            }
            // 어제 발전량 데이터 개수
            long count = inverterDataRepository.countByInverterIdAndTimestampBetween(inverter.getId(),
                    yesterdayStart,
                    yesterdayEnd);

            InverterAccumulation dailyAccumulation = new InverterAccumulation(
                    inverter,
                    lastData.getCumulativeEnergy(),
                    AccumulationType.DAILY,
                    yesterdayEnd,
                    count
            );
            accumulationRepository.save(dailyAccumulation);
        }

        for (JunctionBox junctionBox : junctionBoxes) {
            List<JunctionBoxData> lastData = junctionBoxDataRepository.findByJunctionBoxIdAndTimestampBetween(junctionBox.getId(), yesterdayStart, yesterdayEnd);
            if (lastData != null) {
                Double dailyPower = lastData.stream().mapToDouble(JunctionBoxData::getPower).sum();
                JunctionBoxDataAccumulation dailyAccumulation = new JunctionBoxDataAccumulation(dailyPower, junctionBox, yesterdayEnd, AccumulationType.DAILY);
                junctionBoxDataAccumulationService.save(dailyAccumulation);
            }
        }
    }

    // 매월 1일 자정에 지난 달 누적 발전량을 저장
    @Scheduled(cron = "10 0 0 1 * ?")
    @Transactional
    public void accumulateMonthlyData() {
        log.info("Accumulate monthly data");
        List<Inverter> inverters = inverterRepository.findAll();
        List<JunctionBox> junctionBoxes = junctionBoxRepository.findAll();
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        for (Inverter inverter : inverters) {
            Double monthlyTotal = accumulationRepository.findByInverterIdAndType(inverter.getId(), AccumulationType.DAILY).stream()
                    .filter(acc -> acc.getDate().getMonth() == lastMonth.getMonth() && acc.getDate().getYear() == lastMonth.getYear())
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();

            InverterAccumulation monthlyAccumulation = new InverterAccumulation(
                    inverter,
                    monthlyTotal,
                    AccumulationType.MONTHLY,
                    lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(23, 59)
            );

            accumulationRepository.save(monthlyAccumulation);
        }

        for (JunctionBox junctionBox : junctionBoxes) {
            double sum = junctionBoxDataAccumulationService.findDailyLastCumulativeEnergyByMonth(junctionBox.getId(), lastMonth).stream()
                    .mapToDouble(JunctionBoxDataAccumulation::getCumulativeEnergy)
                    .sum();
            JunctionBoxDataAccumulation junctionBoxDataAccumulation = new JunctionBoxDataAccumulation(sum, junctionBox,
                    lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(23, 59),
                    AccumulationType.MONTHLY);
            junctionBoxDataAccumulationService.save(junctionBoxDataAccumulation);
        }
    }
}
