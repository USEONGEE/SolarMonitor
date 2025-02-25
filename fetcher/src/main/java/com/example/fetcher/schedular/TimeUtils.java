package com.example.fetcher.schedular;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class TimeUtils {

    @Getter
    @AllArgsConstructor
    public static class LocalDateTimeRange {
        private final LocalDateTime start;
        private final LocalDateTime end;
    }

    // 특정 시간이 주어지면 그 날의 시작과 끝을 제공
    public static LocalDateTimeRange getDayRangeBy(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);
        return new LocalDateTimeRange(start, end);
    }

    public static LocalDateTimeRange getHourRangeBy(LocalDateTime time) {
        LocalDateTime start = time.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1).minusSeconds(1);
        return new LocalDateTimeRange(start, end);
    }

    public static LocalDateTimeRange getMonthRangeBy(LocalDate date) {
        LocalDateTime start = date.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return new LocalDateTimeRange(start, end);
    }
}
