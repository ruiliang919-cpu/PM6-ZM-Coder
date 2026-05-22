package com.ruoyi.utils.device.time;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public List<Stats> getAddress() {
        List<Stats> statsList = new ArrayList<>();
        TimerAspect.address.forEach(address -> {
            Stats stats = getStats(address);
            statsList.add(stats);
        });
        return statsList;
    }

    private Stats getStats(String addr) {
        String key = TimerAspect.buildRedisKey(addr);
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);

        if (range == null || range.isEmpty()) return new Stats(addr, 0, 0, 0);

        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long sum = 0;
        int count = 0;

        for (Object o : range) {
            long value = Long.parseLong(o + "");
            max = Math.max(max, value);
            min = Math.min(min, value);
            sum += value;
            count++;
        }

        return new Stats(addr, max, min, (double) sum / count);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private String addr;
        private long max;
        private long min;
        private double avg;
    }


    public static long second() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime targetTime = LocalTime.of(0, 10);
        LocalDateTime nextTarget;
        if (now.toLocalTime().isBefore(targetTime))
            nextTarget = now.withHour(0).withMinute(10).withSecond(0).withNano(0);
        else nextTarget = now.plusDays(1).withHour(0).withMinute(10).withSecond(0).withNano(0);
        return Duration.between(now, nextTarget).getSeconds();
    }

    public static long[] getCurrentWeekRange() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        return new long[]{
            startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000,
            endOfWeek.atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000
        };
    }

    public static long[] getCurrentMonthRange() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        return new long[]{
            startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000,
            endOfMonth.atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000
        };
    }

    public static long[] getCurrentQuarterRange() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        Month firstMonthOfQuarter = Month.of(((month - 1) / 3) * 3 + 1);
        Month lastMonthOfQuarter = firstMonthOfQuarter.plus(2);

        LocalDate startOfQuarter = today.withMonth(firstMonthOfQuarter.getValue()).withDayOfMonth(1);
        LocalDate endOfQuarter = today.withMonth(lastMonthOfQuarter.getValue())
            .with(TemporalAdjusters.lastDayOfMonth());

        return new long[]{
            startOfQuarter.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000,
            endOfQuarter.atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000
        };
    }

    public static long[] getCurrentYearRange() {
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());

        return new long[]{
            startOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000,
            endOfYear.atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000
        };
    }
}
