package com.example.web.service;

import com.example.web.dto.InverterAccumulationDto;
import com.example.web.entity.AccumulationType;
import com.example.web.entity.InverterAccumulation;
import com.example.web.repository.InverterAccumulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InverterAccumulationService {

    private final InverterAccumulationRepository accumulationRepository;

    /**
     * 시간별 발전량 조회
     * @param inverterId 인버터 ID
     * @param date 날짜
     * @return 시간별 발전량 리스트
     */
    public List<InverterAccumulationDto> getHourlyAccumulations(Long inverterId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59, 999_999_999);

        List<InverterAccumulation> list = accumulationRepository
                .findByInverterIdAndTypeAndDateBetween(
                        inverterId, AccumulationType.HOURLY, start, end
                );

        // 1) 시간순 정렬
        list.sort(Comparator.comparing(InverterAccumulation::getDate));

        // 2) 이전 누적값을 빼서 Δ 계산
        List<InverterAccumulationDto> result = new ArrayList<>();
        Double prevCum = null;
        for (InverterAccumulation e : list) {
            double delta = (prevCum == null)
                    ? e.getCumulativeEnergy()    // 최초 시점: 누적값 그대로
                    : e.getCumulativeEnergy() - prevCum;
            prevCum = e.getCumulativeEnergy();

            result.add(new InverterAccumulationDto(
                    inverterId,
                    delta,
                    e.getDate(),
                    AccumulationType.HOURLY
            ));
        }
        return result;
    }

    public List<InverterAccumulationDto> getDailyAccumulations(Long inverterId, LocalDate month) {
        LocalDateTime start = month.atStartOfDay();
        LocalDateTime end = month
                .withDayOfMonth(month.lengthOfMonth())
                .atTime(23, 59, 59, 999_999_999);

        List<InverterAccumulation> list = accumulationRepository
                .findByInverterIdAndTypeAndDateBetween(
                        inverterId, AccumulationType.DAILY, start, end
                );

        list.sort(Comparator.comparing(InverterAccumulation::getDate));

        List<InverterAccumulationDto> result = new ArrayList<>();
        Double prevCum = null;
        for (InverterAccumulation e : list) {
            double delta = (prevCum == null)
                    ? e.getCumulativeEnergy()
                    : e.getCumulativeEnergy() - prevCum;
            prevCum = e.getCumulativeEnergy();

            result.add(new InverterAccumulationDto(
                    inverterId,
                    delta,
                    e.getDate(),
                    AccumulationType.DAILY
            ));
        }
        return result;
    }

    public List<InverterAccumulationDto> getMonthlyAccumulations(Long inverterId, int year) {
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, 12, 31).atTime(23, 59, 59, 999_999_999);

        List<InverterAccumulation> list = accumulationRepository
                .findByInverterIdAndTypeAndDateBetween(
                        inverterId, AccumulationType.MONTHLY, start, end
                );

        list.sort(Comparator.comparing(InverterAccumulation::getDate));

        List<InverterAccumulationDto> result = new ArrayList<>();
        Double prevCum = null;
        for (InverterAccumulation e : list) {
            double delta = (prevCum == null)
                    ? e.getCumulativeEnergy()
                    : e.getCumulativeEnergy() - prevCum;
            prevCum = e.getCumulativeEnergy();

            result.add(new InverterAccumulationDto(
                    inverterId,
                    delta,
                    e.getDate(),
                    AccumulationType.MONTHLY
            ));
        }
        return result;
    }

    // 이번 달 누적 발전량 조회
    public Double getThisMonthAccumulation(Long inverterId) {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59, 999999999);

        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                inverterId,
                AccumulationType.DAILY,
                startDateTime,
                endDateTime
        );

        return monthlyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }

    // 저번 달까지의 누적 발전량
    public Double getAccumulationUntilLastMonth(Long inverterId) {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).minusMonths(1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().withDayOfMonth(1).minusDays(1).atTime(23, 59, 59, 999999999);

        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                inverterId,
                AccumulationType.DAILY,
                startDateTime,
                endDateTime
        );

        return monthlyAccumulations.stream()
                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
                .sum();
    }
}
