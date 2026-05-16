package com.ruoyi.modbus.util;

import com.ruoyi.cache.Key;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.modbus.util.Register.FORMATTER;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordPlus {
    private final Key k;
    private final ModbusTCPManager m;
    private final DListUtil d;
    private final RedisTemplate<String, Object> r;
    private final ExecutorService e = Executors.newFixedThreadPool(5);
    private static final String[] SCENE_ARR = {"", "0XAAEE", "0XAB2E", "0XAB6E"};
    public static final String RECORD_KEY = "zm:record:module~";

    public void runModule() {
//        Set<String> keys = r.keys(RECORD_KEY + "*");
//        if (!keys.isEmpty()) r.delete(keys);
        d.Nos().forEach(n -> e.submit(() -> {
            try {
                if (k.getTelecommand(Math.toIntExact(n), 164) == 0) {
                    runTime(Math.toIntExact(n));
                }
            } catch (Exception ignored) {
            }
        }));
    }

    private void runTime(int no) {
        short[] enabled = k.getRemoteByArr(no, "0XABAF");
        if (enabled[0] != -1) {
            List<DevConfigTimeControlScene> scenes = (List<DevConfigTimeControlScene>) k.getRemote(no, SCENE_ARR[enabled[0]]);
            if (null != scenes && !scenes.isEmpty()) {
                for (DevConfigTimeControlScene s : scenes) {
                    if (1 == s.getEnabledStatus()) {
                        LocalTime sTime = LocalTime.parse(s.getStime(), FORMATTER);
                        if (sTime.isAfter(LocalTime.now())) {
                            Duration duration = Duration.between(LocalTime.now(), sTime);
                            r.opsForValue().set(RECORD_KEY + no, "", duration.get(SECONDS), TimeUnit.SECONDS);
                            return;
                        }
                    }
                }
            }
        }
    }
}

