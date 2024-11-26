package com.energy.outsourcing.service;

import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.JunctionBoxDataAccumulation;
import com.energy.outsourcing.repository.JunctionBoxDataAccumulationRepository;
import com.energy.outsourcing.schedular.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JunctionBoxDataAccumulationService {
    private final JunctionBoxDataAccumulationRepository junctionBoxDataAccumulationRepository;
    private final JunctionBoxDataService junctionBoxDataService;

    @Transactional
    public JunctionBoxDataAccumulation save(JunctionBoxDataAccumulation junctionBoxDataAccumulation) {
        return junctionBoxDataAccumulationRepository.save(junctionBoxDataAccumulation);
    }

    // 특정 월을 입력받으면 해당 월의 일별 마지막 누적 저장량을 반환
    public List<JunctionBoxDataAccumulation> findDailyLastCumulativeEnergyByMonth(Long junctionBoxId,
                                                                                  LocalDate localDate) {
        TimeUtils.LocalDateTimeRange monthRangeBy = TimeUtils.getMonthRangeBy(localDate);
        LocalDateTime start = monthRangeBy.getStart();
        LocalDateTime end = monthRangeBy.getEnd();
        return junctionBoxDataAccumulationRepository.findByTimestampBetweenAndTypeAndJunctionBoxId(start,
                end,
                AccumulationType.DAILY,
                junctionBoxId);
    }

    // 현재 시점의 모든 달의 누적 발전량의 합, 이번달의 모든 일의 발전량합을 계산해서 반환
    public Double calculateTotalCumulativeEnergyUntilYesterday(Long junctionBoxId) {
        // 지난달까지의 월별 발전량 합계
        double lastMonthsSum = junctionBoxDataAccumulationRepository.findAllByJunctionBoxIdAndType(junctionBoxId, AccumulationType.MONTHLY)
                .stream()
                .mapToDouble(JunctionBoxDataAccumulation::getCumulativeEnergy)
                .sum();
        // 이번달의 일별 발전량 합계
        LocalDate localDate = LocalDateTime.now().toLocalDate();
        TimeUtils.LocalDateTimeRange monthRangeBy = TimeUtils.getMonthRangeBy(localDate);
        LocalDateTime start = monthRangeBy.getStart();
        LocalDateTime end = monthRangeBy.getEnd();
        double thisMonthSum = junctionBoxDataAccumulationRepository.findByTimestampBetweenAndTypeAndJunctionBoxId(start,
                        end,
                        AccumulationType.DAILY,
                        junctionBoxId)
                .stream()
                .mapToDouble(JunctionBoxDataAccumulation::getCumulativeEnergy)
                .sum();
        return lastMonthsSum + thisMonthSum;
    }

}
