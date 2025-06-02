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
import java.util.Optional;
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
            double delta;
            if (prevCum == null) {
                // 첫 시점(00시)의 발전량은 항상 0으로
                delta = 0.0;
            } else {
                delta = e.getCumulativeEnergy() - prevCum;
            }
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
        // 1) 조회 범위 설정 (5월 1일 00:00 ~ 5월 마지막일 23:59:59)
        LocalDateTime start = month.atStartOfDay();
        LocalDateTime end = month
                .withDayOfMonth(month.lengthOfMonth())
                .atTime(23, 59, 59, 999_999_999);

        // 2) “이전 일자” 누적값(4월 30일 23:59:59) 미리 조회
        Optional<InverterAccumulation> prevOpt = accumulationRepository
                .findTopByInverterIdAndTypeAndDateBeforeOrderByDateDesc(
                        inverterId,
                        AccumulationType.DAILY,
                        start
                );
        Double prevCum = prevOpt.map(InverterAccumulation::getCumulativeEnergy).orElse(null);

        // 3) 실제 5월 데이터 조회
        List<InverterAccumulation> list = accumulationRepository
                .findByInverterIdAndTypeAndDateBetween(
                        inverterId, AccumulationType.DAILY, start, end
                );

        // 4) 시간순 정렬
        list.sort(Comparator.comparing(InverterAccumulation::getDate));

        // 5) Δ 계산
        List<InverterAccumulationDto> result = new ArrayList<>();
        for (InverterAccumulation e : list) {
            double delta;
            if (prevCum == null) {
                // (1) 이전 누적값이 없으면 이 인버터가 첫 스냅샷일 수도 있으므로,
                //     기존 로직처럼 “현재 누적값 전체”를 쓰거나 0으로 초기화할지 선택
                //     → 보통은 ‘e.getCumulativeEnergy()’를 Δ로 쓰지만, 만약 100% 첫 스냅샷을 0으로 처리하려면 delta=0.0
                delta = e.getCumulativeEnergy();
            } else {
                // (2) 정상: (5월 1일 누적 – 4월 30일 누적)
                delta = e.getCumulativeEnergy() - prevCum;
            }
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
        // 1) 조회 범위: 2025-01-01 00:00:00 ~ 2025-12-31 23:59:59
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, 12, 31).atTime(23, 59, 59, 999_999_999);

        // 2) 이전 월(2024-12-xx) 누적값 조회
        Optional<InverterAccumulation> prevOpt = accumulationRepository
                .findTopByInverterIdAndTypeAndDateBeforeOrderByDateDesc(
                        inverterId,
                        AccumulationType.MONTHLY,
                        start
                );
        Double prevCum = prevOpt.map(InverterAccumulation::getCumulativeEnergy).orElse(null);

        // 3) 2025년 월별 스냅샷 조회
        List<InverterAccumulation> list = accumulationRepository
                .findByInverterIdAndTypeAndDateBetween(
                        inverterId, AccumulationType.MONTHLY, start, end
                );

        // 4) 날짜순 정렬
        list.sort(Comparator.comparing(InverterAccumulation::getDate));

        // 5) Δ 계산
        List<InverterAccumulationDto> result = new ArrayList<>();
        for (InverterAccumulation e : list) {
            double delta;
            if (prevCum == null) {
                // 인버터 첫 월 스냅샷인 경우: 논리에 따라 0 또는 e.getCumulativeEnergy() 선택
                delta = e.getCumulativeEnergy();
            } else {
                delta = e.getCumulativeEnergy() - prevCum;
            }
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

//    // 이번 달 누적 발전량 조회
//    public Double getThisMonthAccumulation(Long inverterId) {
//        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
//        LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59, 999999999);
//
//        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
//                inverterId,
//                AccumulationType.DAILY,
//                startDateTime,
//                endDateTime
//        );
//
//        return monthlyAccumulations.stream()
//                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
//                .sum();
//    }
//
//    // 저번 달까지의 누적 발전량
//    public Double getAccumulationUntilLastMonth(Long inverterId) {
//        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).minusMonths(1).atStartOfDay();
//        LocalDateTime endDateTime = LocalDate.now().withDayOfMonth(1).minusDays(1).atTime(23, 59, 59, 999999999);
//
//        List<InverterAccumulation> monthlyAccumulations = accumulationRepository.findByInverterIdAndTypeAndDateBetween(
//                inverterId,
//                AccumulationType.DAILY,
//                startDateTime,
//                endDateTime
//        );
//
//        return monthlyAccumulations.stream()
//                .mapToDouble(InverterAccumulation::getCumulativeEnergy)
//                .sum();
//    }
}
