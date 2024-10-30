package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.InverterAccumulationDto;
import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.InverterAccumulation;
import com.energy.outsourcing.repository.InverterAccumulationRepository;
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
}
