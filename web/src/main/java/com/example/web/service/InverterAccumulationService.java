package com.example.web.service;

import com.example.web.dto.InverterAccumulationDto;
import com.example.web.entity.AccumulationType;
import com.example.web.entity.InverterAccumulation;
import com.example.web.repository.InverterAccumulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59, 999999999);

        List<InverterAccumulation> hourlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                inverterId,
                AccumulationType.HOURLY,
                startDateTime,
                endDateTime
        );

        return hourlyAccumulations.stream()
                .map(InverterAccumulationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 일별 발전량 조회
     * @param inverterId 인버터 ID
     * @param month 월의 첫 날
     * @return 일별 발전량 리스트
     */
    public List<InverterAccumulationDto> getDailyAccumulations(Long inverterId, LocalDate month) {
        LocalDateTime startDateTime = month.atStartOfDay();
        LocalDateTime endDateTime = month.withDayOfMonth(month.lengthOfMonth()).atTime(23, 59, 59, 999999999);

        List<InverterAccumulation> dailyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                inverterId,
                AccumulationType.DAILY,
                startDateTime,
                endDateTime
        );

        return dailyAccumulations.stream()
                .map(InverterAccumulationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 월별 발전량 조회
     * @param inverterId 인버터 ID
     * @param year 연도
     * @return 월별 발전량 리스트
     */
    public List<InverterAccumulationDto> getMonthlyAccumulations(Long inverterId, int year) {
        LocalDateTime startDateTime = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.of(year, 12, 31).atTime(23, 59, 59, 999999999);

        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
                inverterId,
                AccumulationType.MONTHLY,
                startDateTime,
                endDateTime
        );

        return monthlyAccumulations.stream()
                .map(InverterAccumulationDto::fromEntity)
                .collect(Collectors.toList());
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
