package com.energy.outsourcing.schedular;
import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.InverterAccumulation;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
import com.energy.outsourcing.repository.InverterDataRepository;
import com.energy.outsourcing.repository.InverterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InverterAccumulationScheduler {

    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;
    private final InverterRepository inverterRepository;

    // 매 시간마다 전 시간의 마지막 데이터를 저장
    @Scheduled(cron = "0 0 * * * ?") // 매 정각마다 실행
    @Transactional
    public void accumulateHourlyData() {
        List<Inverter> inverters = inverterRepository.findAll();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES); // 현재 시각의 분 이하 제거
        LocalDateTime lastHourEnd = now.minusHours(1).withMinute(59); // 이전 시간의 끝 시각

        for (Inverter inverter : inverters) {
            InverterData lastData = inverterDataRepository.findLastInverterDataByInverterId(inverter.getId(), lastHourEnd)
                    .orElse(null);
            if (lastData != null) {
                InverterAccumulation hourlyAccumulation = new InverterAccumulation();
                hourlyAccumulation.setInverter(inverter);
                hourlyAccumulation.setCumulativeEnergy(lastData.getCumulativeEnergy());
                hourlyAccumulation.setDate(lastHourEnd); // 날짜는 이전 시간의 날짜
                hourlyAccumulation.setType(AccumulationType.HOURLY);

                accumulationRepository.save(hourlyAccumulation);
            }
        }
    }
    // 매일 자정에 전날 마지막 누적 발전량을 저장
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void accumulateDailyData() {
        List<Inverter> inverters = inverterRepository.findAll();
        LocalDate today = LocalDate.now();
        // 어제 시작 시간
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();
        // 어제 끝 시간
        LocalDateTime yesterdayEnd = today.atStartOfDay().minusSeconds(10);

        for (Inverter inverter : inverters) {
            InverterData lastData = inverterDataRepository.findLastInverterDataByInverterId(inverter.getId(), yesterdayEnd)
                    .orElseThrow(() -> new RuntimeException("InverterData not found"));
            if (lastData != null) {
                InverterAccumulation dailyAccumulation = new InverterAccumulation();
                dailyAccumulation.setInverter(inverter);
                dailyAccumulation.setCumulativeEnergy(lastData.getCumulativeEnergy());
                // 어제 날짜 저장하기
                dailyAccumulation.setDate(yesterdayEnd);
                dailyAccumulation.setType(AccumulationType.DAILY);

                accumulationRepository.save(dailyAccumulation);
            }
        }

        
    }

    // 매월 1일 자정에 지난 달 누적 발전량을 저장
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void accumulateMonthlyData() {
        List<Inverter> inverters = inverterRepository.findAll();
        LocalDate lastMonth = LocalDate.now().minusMonths(10);

        for (Inverter inverter : inverters) {
            Double monthlyTotal = accumulationRepository.findByInverterIdAndType(inverter.getId(), AccumulationType.DAILY).stream()
                    .filter(acc -> acc.getDate().getMonth() == lastMonth.getMonth() && acc.getDate().getYear() == lastMonth.getYear())
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();

            InverterAccumulation monthlyAccumulation = new InverterAccumulation();
            monthlyAccumulation.setInverter(inverter);
            monthlyAccumulation.setCumulativeEnergy(monthlyTotal);
            // 이전 달의 마지막 날짜로 설정
            monthlyAccumulation.setDate(lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(23, 59));
            monthlyAccumulation.setType(AccumulationType.MONTHLY);

            accumulationRepository.save(monthlyAccumulation);
        }
    }
}
