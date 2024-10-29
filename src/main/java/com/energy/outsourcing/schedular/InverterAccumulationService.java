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
import java.util.List;

@Service
@RequiredArgsConstructor
public class InverterAccumulationService {

    private final InverterDataRepository inverterDataRepository;
    private final InverterAccumulationRepository accumulationRepository;
    private final InverterRepository inverterRepository;

    // 매일 자정에 전날 마지막 누적 발전량을 저장
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void accumulateDailyData() {
        List<Inverter> inverters = inverterRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDateTime yesterdayEnd = today.atStartOfDay().minusSeconds(1);

        for (Inverter inverter : inverters) {
            InverterData lastData = inverterDataRepository.findLastInverterDataByInverterId(inverter.getId(), yesterdayEnd)
                    .orElseThrow(() -> new RuntimeException("InverterData not found"));
            if (lastData != null) {
                InverterAccumulation dailyAccumulation = new InverterAccumulation();
                dailyAccumulation.setInverter(inverter);
                dailyAccumulation.setCumulativeEnergy(lastData.getCumulativeEnergy());
                dailyAccumulation.setDate(today.minusDays(1)); // 어제 날짜
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
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        for (Inverter inverter : inverters) {
            Double monthlyTotal = accumulationRepository.findByInverterIdAndType(inverter.getId(), AccumulationType.DAILY).stream()
                    .filter(acc -> acc.getDate().getMonth() == lastMonth.getMonth() && acc.getDate().getYear() == lastMonth.getYear())
                    .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                    .sum();

            InverterAccumulation monthlyAccumulation = new InverterAccumulation();
            monthlyAccumulation.setInverter(inverter);
            monthlyAccumulation.setCumulativeEnergy(monthlyTotal);
            monthlyAccumulation.setDate(lastMonth.withDayOfMonth(1)); // 이전 달의 첫 날로 저장
            monthlyAccumulation.setType(AccumulationType.MONTHLY);

            accumulationRepository.save(monthlyAccumulation);
        }
    }
}
