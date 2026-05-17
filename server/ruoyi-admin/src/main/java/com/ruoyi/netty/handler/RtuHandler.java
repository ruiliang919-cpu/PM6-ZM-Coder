package com.ruoyi.netty.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.DeviceUtilCache;
import com.ruoyi.cache.ZoneUtilCache;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevLightZoneCombination;
import com.ruoyi.zm.mapper.DevLightZoneCombinationMapper;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RtuHandler {

    private final ZoneUtilCache zoneUtilCache;
    private final DeviceUtilCache deviceUtilCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RtuWriteUtil rtuWriteUtil;
    private final DevLightZoneCombinationMapper lightZoneCombinationMapper;
    private final DListUtil dListUtil;


    // 处理上位机发送帧
    public String handleReceivedData(String frame) {
        if (frame == null || frame.length() < 8) {
            log.warn("RTU帧长度非法, length={}, frame={}", frame == null ? 0 : frame.length(), frame);
            return "帧长度非法";
        }
        String result = "Hello World!";
        try {
            String[] frameArr = ScaleUtil.splitIntoPairs(frame.substring(0, frame.length() - 4));
            // crc 校验码
            int crc = Integer.parseInt(frame.substring(frame.length() - 4), 16);
            if (crc != ScaleUtil.crcCheck(frameArr)) {
                // crc 校验出错
                return result = "crc 校验出错";
            }
            // 功能码
            String code = frameArr[1];
            // 寄存器地址
            int address = Integer.parseInt((frameArr[2] + frameArr[3]), 16);
            // 操作寄存器数量
            int num = Integer.parseInt((frameArr[4] + frameArr[5]), 16);

            // 03 功能码 遥测
            if (3 == Integer.parseInt(code)) {
                result = process(address, num);
            }
            // 10 功能码 遥控
            else if (10 == Integer.parseInt(code)) {
                // 遥控携带数据
                if (frame.length() < 18) {
                    log.warn("RTU帧数据段长度不足, length={}", frame.length());
                    return "帧数据不完整";
                }
                String data = frame.substring(14, 18);
                result = process(address, data);
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            log.error("RTU帧解析异常, frame={}", frame, e);
            return "帧格式错误";
        }
        return result;
    }

    // 03 功能码处理器
    public String process(int address, int num) {
        String result = "查询异常";
        String head = "0103" + ScaleUtil.to4s(num * 2);
        try {
            switch (address) {
                // 0-就地/1-远程
                case 40001: {
                    String select = (String) redisTemplate.opsForValue().get("zm:global:module:select");
                    if (select != null && !"".equals(select)) {
                        if ("on-the-spot".equals(select)) {
                            result = head + "0000";
                        } else if ("on-the-line".equals(select)) {
                            result = head + "0001";
                        }
                    } else {
                        result = head + "0000";
                    }
                    result = (result + Integer.toHexString(ScaleUtil.crcCheck(result))).toUpperCase();
                    break;
                }
                // 备用
                case 40002: {
                    break;
                }
                // 场景模式
                case 40003: {
                    String select = (String) redisTemplate.opsForValue().get("zm:global:scene:select");
                    if (select != null && !"".equals(select)) {
                        int selectInt = Integer.parseInt(select);
                        StringBuilder temp = new StringBuilder();
                        for (int i = 16; i > 0; i--) {
                            if (i == selectInt) {
                                temp.append("1");
                            } else {
                                temp.append("0");
                            }
                        }
                        result = head + ScaleUtil.binaryToHex(temp.toString(), num * 4);
                    } else {
                        result = head + "0000";
                    }
                    result = (result + Integer.toHexString(ScaleUtil.crcCheck(result))).toUpperCase();
                    break;
                }
                // 区间照明回路状态
                case 40004: {
                    result = getLoopStatus(head, num, Arrays.asList(24, 25));
                    break;
                }
                // 广告照明回路状态
                case 40009: {
                    result = getLoopStatus(head, num, Arrays.asList(26, 27));
                    break;
                }
                // 导向照明回路状态
                case 40014: {
                    result = getLoopStatus(head, num, Arrays.asList(28, 29));
                    break;
                }
                // 出入口地面厅照明回路状态
                case 40019: {
                    result = getLoopStatus(head, num, Arrays.asList(30, 31));
                    break;
                }
                // 直流照明控制柜(箱)故障
                case 40024: {
                    List<Integer> deviceStatus = deviceUtilCache.getDeviceStatus();
                    StringBuilder temp = new StringBuilder();
                    int size = 16 * num;
                    for (int i = size - 1; i >= 0; i--) {
                        if (i < deviceStatus.size()) {
                            temp.append(deviceStatus.get(i));
                        } else {
                            temp.append("0");
                        }
                    }
                    result = head + ScaleUtil.binaryToHex(temp.toString(), num * 4);
                    String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
                    result = (result + crc).toUpperCase();
                    break;
                }
                // 日周月年耗电量
                case 40030: {
                    result = getPower(head, num, "day");
                    break;
                }
                case 40060: {
                    result = getPower(head, num, "week");
                    break;
                }
                case 40090: {
                    result = getPower(head, num, "month");
                    break;
                }
                case 40120: {
                    result = getPower(head, num, "year");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("RtuHandler → 03H 查询出错，寄存器地址：{}, 寄存器数量：{}", address, num, e);
        }
        return result;
    }

    // 03 功能码，根据分区获取回路照明状态
    private String getLoopStatus(String head, int num, List<Integer> zoneIds) {
        List<ZoneUtilCache.RespData> data = zoneUtilCache.setData(zoneIds);
        StringBuilder temp = new StringBuilder();
        int size = 16 * num;
        for (int i = size - 1; i >= 0; i--) {
            if (i < data.size()) {
                temp.append(data.get(i).getLoopStatus());
            } else {
                temp.append("0");
            }
        }
        String result = head + ScaleUtil.binaryToHex(temp.toString(), num * 4);
        String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
        return (result + crc).toUpperCase();
    }

    // 获取所有机柜的交流回路照明状态
    public String getAcLoopStatus() {
        // 获取每个机柜的设备号
        List<Long> deviceNos = dListUtil.Nos();
        List<Boolean> data = zoneUtilCache.getAcLoopStatus(deviceNos);
        StringBuilder temp = new StringBuilder();
        for (int i = 80 - 1; i >= 0; i--) {
            if (i < data.size()) {
                temp.append(data.get(i) ? 1 : 0);
            } else temp.append("0");
        }
        return temp.toString();
    }

    // 根据分区获取直流回路照明状态
    public String getDcLoopStatus(int type) {
        List<Integer> zoneIds = new ArrayList<>();
        DevLightZoneCombination source = lightZoneCombinationMapper.selectOne(new LambdaQueryWrapper<DevLightZoneCombination>()
            .eq(DevLightZoneCombination::getOrderNo, type));
        if (source != null) {
            String zoneList = source.getZoneList();
            if (zoneList != null && !zoneList.isEmpty()) {
                for (String zoneId : zoneList.split(",")) {
                    zoneIds.add(Integer.parseInt(zoneId));
                }
            }
        }

        List<ZoneUtilCache.RespData> data = zoneUtilCache.setData(zoneIds);
        StringBuilder temp = new StringBuilder();
        for (int i = 80 - 1; i >= 0; i--) {
            if (i < data.size()) {
                temp.append(data.get(i).getLoopStatus());
            } else {
                temp.append("0");
            }
        }
        return temp.toString();
    }

    // 获取设备故障信息
    public String getFault() {
        List<Integer> deviceStatus = deviceUtilCache.getDeviceStatus();
        StringBuilder temp = new StringBuilder();
        for (int i = 96 - 1; i >= 0; i--) {
            if (i < deviceStatus.size()) {
                temp.append(deviceStatus.get(i));
            } else {
                temp.append("0");
            }
        }
        // System.out.println(temp.toString());
        return temp.toString();
    }

    // 03 功能码，根据类型获取耗电量 电源柜+分区组合
    private String getPower(String head, int num, String type) {
        List<Long> data = deviceUtilCache.getRtuData(type);
        List<Long> tempList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < num; i++) tempList.add(data.get(i));
        Collections.reverse(tempList);
        for (Long aLong : tempList) {
            long high = (aLong >> 16) & 0xFFFF;
            long low = aLong & 0xFFFF;
            String byteStr = String.format("%4s", Long.toHexString(high) + Long.toHexString(low)).replace(' ', '0').toUpperCase();
            result.append(byteStr);
        }
        result = new StringBuilder(head + result);
        String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result.toString())).toUpperCase()).replace(' ', '0');
        return (result + crc).toUpperCase();
    }

    public List<Short> getPower(String type) {
        List<Long> data = deviceUtilCache.getRtuData(type);
        List<Long> tempList = new ArrayList<>();
        List<Short> result = new ArrayList<>();
        for (int i = 0; i < 15; i++) tempList.add(data.get(i));
        for (Long a : tempList) {
            short high = (short) ((int) (a >> 16) & 0xFFFF);
            short low = (short) (a & 0xFFFF);
            result.add(high);
            result.add(low);
        }
        return result;
    }

    // 10 功能码处理器
    public String process(int address, String data) {
        String result = "查询异常";
        String head = "0110";
        // 如果是就地模式，不允许下发指令
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        boolean flag = module == null || module.isEmpty() || "on-the-spot".equals(module);
        if (flag) return result;
        switch (address) {
            // 场景模式
            case 40200: {
                int sceneId = 0;
                String[] sceneSelectArr = ScaleUtil.HexToBinaryArr(data);
                for (int i = 0; i < sceneSelectArr.length; i++) {
                    try {
                        if (1 == Integer.parseInt(sceneSelectArr[i])) {
                            sceneId = 16 - i;
                        }
                    } catch (Exception ignored) {

                    }
                }
                if (sceneId != 0) {
                    rtuWriteUtil.intoScenes(sceneId);
                }
                result = head + "9D080001";
                String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
                result = result + crc;
                break;
            }
            // 区间照明总控制
            case 40201: {
                rtuWriteUtil.loopControl(data, 24, 25);
                result = head + "9D090001";
                String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
                result = result + crc;
                break;
            }
            // 广告照明总控制case 40202
            case 40202: {
                rtuWriteUtil.loopControl(data, 26, 27);
                result = head + "9D0A0001";
                String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
                result = result + crc;
                break;
            }
            // 导向照明总控制
            case 40203: {
                rtuWriteUtil.loopControl(data, 28, 29);
                result = head + "9D0B0001";
                String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
                result = result + crc;
                break;
            }
            // 出入口地面厅照明总控制
            case 40204: {
                rtuWriteUtil.loopControl(data, 30, 31);
                result = head + "9D0C0001";
                String crc = String.format("%4s", Integer.toHexString(ScaleUtil.crcCheck(result)).toUpperCase()).replace(' ', '0');
                result = result + crc;
                break;
            }
        }
        return result;
    }

}
