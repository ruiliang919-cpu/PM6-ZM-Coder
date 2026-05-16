package com.ruoyi.utils.dnb;

import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.vo.DnbVO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class DNBUtil {

    public static <T> CompletableFuture<List<DnbVO>> getDNBGeneric(
        DevBaseDevice device,
        Function<Long, List<T>> dataFetcher,
        ToLongFunction<T> timestampGetter,
        Function<T, Integer> energyMeterNoGetter,
        Function<T, BigDecimal> powerGetter,
        DateTimeFormatter formatter
    ) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> dataList = dataFetcher.apply(device.getDeviceNo());
            if (dataList.isEmpty()) return Collections.emptyList();
            return dataList.stream().map(item -> {
                DnbVO vo = new DnbVO();
                vo.setName(device.getDeviceName());
                vo.setNo(energyMeterNoGetter.apply(item));
                vo.setTime(
                    Instant.ofEpochMilli(timestampGetter.applyAsLong(item) * 1000L)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(formatter)
                );
                vo.setPower(powerGetter.apply(item));
                return vo;
            }).collect(Collectors.toList());
        });
    }
}
