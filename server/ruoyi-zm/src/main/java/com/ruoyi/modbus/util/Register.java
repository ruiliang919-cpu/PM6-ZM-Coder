package com.ruoyi.modbus.util;

import com.ruoyi.cache.DeviceUtilCache;
import com.ruoyi.cache.Key;
import com.ruoyi.netty.handler.RtuHandler;
import com.ruoyi.netty.handler.RtuWriteUtil;
import com.ruoyi.schedule.InstructionQueue;
import com.ruoyi.schedule.TelemeterSendSchedule;
import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import com.serotonin.modbus4j.BasicProcessImage;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 寄存器及线圈
@Data
@Slf4j
@Accessors(chain = true)
public class Register {
    private static final Map<Integer, Short> data = new ConcurrentHashMap<>();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private Key key;
    private RtuHandler rtuHandler;
    private RtuWriteUtil rtuWriteUtil;
    private DeviceUtilCache deviceUtilCache;
    private RedisTemplate<String, Object> redisTemplate;
    private int start;
    private TelemeterSendSchedule send03Schedule;
    private InstructionQueue queue;

    public BasicProcessImage getImg(int slaveId) {
        // 初始化过程影像区
        BasicProcessImage processImage = new BasicProcessImage(slaveId);
        processImage.setInvalidAddressValue(Short.MIN_VALUE);
        for (int i = start; i < start + 600; i++) {
            data.put(i, (short) 0);
            processImage.setHoldingRegister(i, data.get(i));
        }
        processImage.addListener(new BasicProcessImageListener(rtuWriteUtil, start, redisTemplate));
        startPeriodicUpdate(processImage);
        return processImage;
    }

    String[] arr = {"", "0XAAEE", "0XAB2E", "0XAB6E"};

    // 获取主机模式
    public short globalModule() {
        // 就地/远程模式
        String select = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        if (select != null && !select.isEmpty()) {
            if ("on-the-spot".equals(select)) return 0;
            else if ("on-the-line".equals(select)) return 1;
        }
        return 0;
    }

    // 定时刷新数据
    private void startPeriodicUpdate(BasicProcessImage processImage) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                try {
                    data.put(start, globalModule());
                    data.put(start + 1, (short) 0);
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    data.put(start + 2, sceneSelect());
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    String loopStatus = rtuHandler.getAcLoopStatus();
                    data.put(start + 3, (short) Integer.parseInt(loopStatus.substring(64, 80), 2));
                    data.put(start + 4, (short) Integer.parseInt(loopStatus.substring(48, 64), 2));
                    data.put(start + 5, (short) Integer.parseInt(loopStatus.substring(32, 48), 2));
                    data.put(start + 6, (short) Integer.parseInt(loopStatus.substring(16, 32), 2));
                    data.put(start + 7, (short) Integer.parseInt(loopStatus.substring(0, 16), 2));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    String loopStatus = rtuHandler.getDcLoopStatus(1);
                    data.put(start + 8, (short) Integer.parseInt(loopStatus.substring(64, 80), 2));
                    data.put(start + 9, (short) Integer.parseInt(loopStatus.substring(48, 64), 2));
                    data.put(start + 10, (short) Integer.parseInt(loopStatus.substring(32, 48), 2));
                    data.put(start + 11, (short) Integer.parseInt(loopStatus.substring(16, 32), 2));
                    data.put(start + 12, (short) Integer.parseInt(loopStatus.substring(0, 16), 2));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    String loopStatus = rtuHandler.getDcLoopStatus(2);
                    data.put(start + 13, (short) Integer.parseInt(loopStatus.substring(64, 80), 2));
                    data.put(start + 14, (short) Integer.parseInt(loopStatus.substring(48, 64), 2));
                    data.put(start + 15, (short) Integer.parseInt(loopStatus.substring(32, 48), 2));
                    data.put(start + 16, (short) Integer.parseInt(loopStatus.substring(16, 32), 2));
                    data.put(start + 17, (short) Integer.parseInt(loopStatus.substring(0, 16), 2));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    String loopStatus = rtuHandler.getDcLoopStatus(3);
                    data.put(start + 18, (short) Integer.parseInt(loopStatus.substring(64, 80), 2));
                    data.put(start + 19, (short) Integer.parseInt(loopStatus.substring(48, 64), 2));
                    data.put(start + 20, (short) Integer.parseInt(loopStatus.substring(32, 48), 2));
                    data.put(start + 21, (short) Integer.parseInt(loopStatus.substring(16, 32), 2));
                    data.put(start + 22, (short) Integer.parseInt(loopStatus.substring(0, 16), 2));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    String fault = rtuHandler.getFault();
                    data.put(start + 23, (short) Integer.parseInt(fault.substring(80, 96), 2));
                    data.put(start + 24, (short) Integer.parseInt(fault.substring(64, 80), 2));
                    data.put(start + 25, (short) Integer.parseInt(fault.substring(48, 64), 2));
                    data.put(start + 26, (short) Integer.parseInt(fault.substring(32, 48), 2));
                    data.put(start + 27, (short) Integer.parseInt(fault.substring(16, 32), 2));
                    data.put(start + 28, (short) Integer.parseInt(fault.substring(0, 16), 2));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    List<Short> day = rtuHandler.getPower("day");
                    for (int i = start + 29; i < start + 59; i++)
                        data.put(i, day.get(i - (start + 29)));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    List<Short> week = rtuHandler.getPower("week");
                    for (int i = start + 59; i < start + 89; i++)
                        data.put(i, week.get(i - (start + 59)));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    List<Short> month = rtuHandler.getPower("month");
                    for (int i = start + 89; i < start + 119; i++)
                        data.put(i, month.get(i - (start + 89)));
                } catch (Exception e) {
                    log.info("", e);
                }
                try {
                    List<Short> year = rtuHandler.getPower("year");
                    for (int i = start + 119; i < start + 149; i++)
                        data.put(i, year.get(i - (start + 119)));
                } catch (Exception e) {
                    log.info("", e);
                }

                try {
                    int[] ints = {6, 5, 7, 4};
                    for (int i = 0; i < ints.length; i++) {
                        String loopStatus = rtuHandler.getDcLoopStatus(ints[i]);
                        int base = start + 149 + i * 5;
                        data.put(base, (short) Integer.parseInt(loopStatus.substring(64, 80), 2));
                        data.put(base + 1, (short) Integer.parseInt(loopStatus.substring(48, 64), 2));
                        data.put(base + 2, (short) Integer.parseInt(loopStatus.substring(32, 48), 2));
                        data.put(base + 3, (short) Integer.parseInt(loopStatus.substring(16, 32), 2));
                        data.put(base + 4, (short) Integer.parseInt(loopStatus.substring(0, 16), 2));
                    }
                } catch (Exception e) {
                    log.info("", e);
                }

                for (int i = start; i < start + 168; i++)
                    processImage.setHoldingRegister(i, data.getOrDefault(i, (short) 0));

                short loopStatus = 0;
                String loopStatusStr = (String) redisTemplate.opsForValue().get("zm:global:loop:status");
                if (loopStatusStr != null && !loopStatusStr.isEmpty()) {
                    if (loopStatusStr.equals("true")) loopStatus = 1;
                    else if (loopStatusStr.equals("false")) loopStatus = 2;
                }
                data.put(1, loopStatus);
                processImage.setHoldingRegister(1, data.getOrDefault(1, (short) 0));
            } catch (Exception e) {
                log.error("Register → startPeriodicUpdate：", e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    // 获取IP最小的配电柜
    // 若是自动模式：对应时段的场景
    // 若是手动模式：全局设置的场景
    public short sceneSelect() {
        try {
            List<Integer> devices = deviceUtilCache.getDeviceNoByType("Distribution-cabinets");
            if (key.getTelecommand(devices.get(0), 164) == 0) {
                short[] enabled = key.getRemoteByArr(devices.get(0), "0XABAF");
                if (enabled[0] != -1) {
                    List<DevConfigTimeControlScene> scenes = (List<DevConfigTimeControlScene>) key.getRemote(devices.get(0), arr[enabled[0]]);
                    LocalTime now = LocalTime.now();
                    for (DevConfigTimeControlScene scene : scenes) {
                        LocalTime sTime = LocalTime.parse(scene.getStime(), FORMATTER);
                        LocalTime eTime = LocalTime.parse(scene.getEtime(), FORMATTER);
                        if (sTime.isBefore(now) && eTime.isAfter(now)) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 16; i > 0; i--) {
                                if (i == scene.getSceneSelect()) {
                                    builder.append("1");
                                } else {
                                    builder.append("0");
                                }
                            }
                            return (short) Integer.parseInt(builder.toString(), 2);
                        }
                    }
                }
            } else {
                String select = (String) redisTemplate.opsForValue().get("zm:global:scene:select");
                if (select != null && !select.isEmpty()) {
                    int selectInt = Integer.parseInt(select);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 16; i > 0; i--) {
                        if (i == selectInt) builder.append("1");
                        else builder.append("0");
                    }
                    return (short) Integer.parseInt(builder.toString(), 2);
                }
            }
        } catch (Exception e) {
            // log.error("Register → sceneSelect", e);
        }
        return 0;
    }

}
