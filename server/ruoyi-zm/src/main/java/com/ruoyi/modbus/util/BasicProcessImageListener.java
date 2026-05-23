package com.ruoyi.modbus.util;

import com.ruoyi.netty.handler.RtuWriteUtil;
import com.serotonin.modbus4j.ProcessImageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// 监听器
@Slf4j
public class BasicProcessImageListener implements ProcessImageListener {
    private static final Pattern NUMERIC = Pattern.compile("^\\d{1,2}$");

    private RtuWriteUtil rtuWriteUtil;
    private int start;
    private RedisTemplate<String, Object> redisTemplate;
    private Map<Integer, String> timeMap = new ConcurrentHashMap<>();

    public BasicProcessImageListener(RtuWriteUtil rtuWriteUtil, int start, RedisTemplate<String, Object> redisTemplate) {
        this.rtuWriteUtil = rtuWriteUtil;
        this.start = start;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void coilWrite(int i, boolean b, boolean b1) {
    }

    @Override
    public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
        if (offset >= start + 205 && offset < start + 211) {
            String value;
            if (newValue < 10) value = "0" + newValue;
            else value = String.valueOf(newValue);
            timeMap.put(offset, value);
            log.info("{}", timeMap);
            if (timeMap.size() == 6) {
                if (Objects.equals(timeMap.get(205), "00") &&
                    Objects.equals(timeMap.get(206), "00") &&
                    Objects.equals(timeMap.get(207), "00") &&
                    Objects.equals(timeMap.get(208), "00") &&
                    Objects.equals(timeMap.get(209), "00") &&
                    Objects.equals(timeMap.get(210), "00")
                ) timeMap.clear();
                else {
                    String year = timeMap.get(205);
                    String month = timeMap.get(206);
                    String day = timeMap.get(207);
                    String hour = timeMap.get(208);
                    String minute = timeMap.get(209);
                    String second = timeMap.get(210);
                    if (NUMERIC.matcher(year).matches() && NUMERIC.matcher(month).matches()
                        && NUMERIC.matcher(day).matches() && NUMERIC.matcher(hour).matches()
                        && NUMERIC.matcher(minute).matches() && NUMERIC.matcher(second).matches()) {
                        setSystemDateTime(year, month, day, hour, minute, second);
                    } else {
                        log.warn("Modbus对时参数校验失败: {}-{}-{} {}:{}:{}", year, month, day, hour, minute, second);
                    }
                    timeMap.clear();
                }
            }
        }
        if (!module()) return;
        if (offset == start + 199) {
            String binaryString = String.format("%16s", Integer.toBinaryString(newValue)).replace(' ', '0');
            binaryString = new StringBuilder(binaryString).reverse().toString();
            String[] arr = binaryString.split("");
            int sceneId = 0;
            for (int i = 0; i < arr.length; i++) {
                if (1 == Integer.parseInt(arr[i]))
                    sceneId = i + 1;
            }
            if (sceneId != 0) rtuWriteUtil.intoScenes(sceneId);
        } else if (offset == start + 200) {
            rtuWriteUtil.acLoopControl(newValue);
        } else if (offset == start + 201) {
            rtuWriteUtil.loopControl(newValue, 1);
        } else if (offset == start + 202) {
            rtuWriteUtil.loopControl(newValue, 2);
        } else if (offset == start + 203) {
            rtuWriteUtil.loopControl(newValue, 3);
        } else if (offset == start + 204) {
            rtuWriteUtil.allLoopControl(newValue);
        }else if (offset == start + 211) {
            // 站厅照明亮度调节
            rtuWriteUtil.loopControlFun(newValue, 6, (int) newValue);
        }else if (offset == start + 212) {
            // 站台照明亮度调节
            rtuWriteUtil.loopControlFun(newValue, 5, (int) newValue);
        }else if (offset == start + 213) {
            // 站台灯带照明亮度调节
            rtuWriteUtil.loopControlFun(newValue, 7, (int) newValue);
        }else if (offset == start + 214) {
            // 站厅照明
            rtuWriteUtil.loopControl(newValue, 6);
        }else if (offset == start + 215) {
            // 站台照明
            rtuWriteUtil.loopControl(newValue, 5);
        }else if (offset == start + 216) {
            // 站台灯带照明
            rtuWriteUtil.loopControl(newValue, 7);
        }else if (offset == start + 217) {
            // 装饰灯照明
            rtuWriteUtil.loopControl(newValue, 4);
        }
    }

    private void setSystemDateTime(String year, String month, String day,
                                    String hour, String minute, String second) {
        try {
            String dateStr = year + "-" + month + "-" + day;
            log.info("Modbus对时 设置日期: {}", dateStr);
            Process dateProcess = new ProcessBuilder("cmd", "/c", "date", dateStr).start();
            int dateExit = dateProcess.waitFor();
            if (dateExit != 0) {
                log.warn("Modbus对时 日期命令 exitCode={}", dateExit);
            }
            String timeStr = hour + ":" + minute + ":" + second + ".00";
            log.info("Modbus对时 设置时间: {}", timeStr);
            Process timeProcess = new ProcessBuilder("cmd", "/c", "time", timeStr).start();
            int timeExit = timeProcess.waitFor();
            if (timeExit != 0) {
                log.warn("Modbus对时 时间命令 exitCode={}", timeExit);
            }
        } catch (IOException e) {
            log.error("Modbus对时失败", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Modbus对时被中断", e);
        }
    }

    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }
}
