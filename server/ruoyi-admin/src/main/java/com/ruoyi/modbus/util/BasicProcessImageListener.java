package com.ruoyi.modbus.util;

import com.ruoyi.netty.handler.RtuWriteUtil;
import com.serotonin.modbus4j.ProcessImageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

// 监听器
@Slf4j
public class BasicProcessImageListener implements ProcessImageListener {
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
            String value = "";
            if (newValue < 10) value = "0" + newValue;
            else value = value + newValue;
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
                    try {
                        String command = "cmd /c date " + timeMap.get(205) + "-" + timeMap.get(206) + "-" + timeMap.get(207);
                        log.info(command);
                        Runtime.getRuntime().exec(command);
                        command = "cmd /c time " + timeMap.get(208) + ":" + timeMap.get(209) + ":" + timeMap.get(210) + ".00";
                        log.info(command);
                        Runtime.getRuntime().exec(command);
                    } catch (IOException e) {
                        log.error("", e);
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

    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }
}
