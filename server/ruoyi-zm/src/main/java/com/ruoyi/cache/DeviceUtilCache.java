package com.ruoyi.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.flag.DeviceFlag;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.web.controller.zm.GroupCalculationController;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBasePower;
import com.ruoyi.zm.domain.vo.CabinetCalculationData;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.mapper.DevBasePowerMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceUtilCache {
    private final Key key;
    private final DevBasePowerMapper powerMapper;
    private final GroupCalculationController calculationController;
    private final DListUtil dListUtil;
    private final List<Temp> tempList = new ArrayList<>();

    // 根据IP地址的大小获取所有的设备号，只获取当前在线设备
    public List<Temp> getDeviceNos() {
        if (ObjectUtils.isEmpty(tempList) || DeviceFlag.Flag(DeviceFlag.LIST)) {
            tempList.clear();
            List<DevBaseDevice> devices = dListUtil.List();
            if (!ObjectUtils.isEmpty(devices) && !devices.isEmpty()) {
                for (DevBaseDevice device : devices) {
                    DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(Math.toIntExact(device.getDeviceNo()));
                    Temp temp = new Temp();
                    temp.setNo(device.getDeviceNo());
                    String[] IpArr = device.getIp().split("\\.");
                    temp.setIp(Long.parseLong(IpArr[0] + IpArr[1] + IpArr[2] + IpArr[3]));
                    temp.setLink(tcpVo.getOnlineStatus() == 1);
                    tempList.add(temp);
                }
                tempList.sort(Comparator.comparingLong(Temp::getIp));
            }
        }
        return tempList;
    }

    // 根据设备号获取设备的状态 0-正常/1-故障
    public List<Integer> getDeviceStatus() {
        List<Integer> result = new ArrayList<>();
        List<Temp> deviceNos = getDeviceNos();
        for (Temp deviceNo : deviceNos) {
            DevBaseDeviceTCPVo createTCP = key.getCreateTCP(Math.toIntExact(deviceNo.getNo()));
            if (createTCP.getOnlineStatus() == null || createTCP.getOnlineStatus() == 0) result.add(1);
            else {
                try {
                    int status = key.getTelecommand(Math.toIntExact(deviceNo.getNo()), 162);
                    result.add(status);
                } catch (Exception ignored) {

                }
            }
            // System.out.println(deviceNos);
        }
        return result;
    }

    // 获取电源柜/配电柜的设备号 依据 getDeviceNos()
    // Distribution-cabinets：配电柜 0
    // Power-cabinets：电源柜 1
    public List<Integer> getDeviceNoByType(String typeTxt) {
        List<Integer> result = new ArrayList<>();
        List<Temp> deviceNos = getDeviceNos();
        if ("Distribution-cabinets".equals(typeTxt)) {
            deviceNos = deviceNos.stream().filter(Temp::getLink).collect(Collectors.toList());
            for (Temp deviceNo : deviceNos) {
                try {
                    int type = key.getTelecommand(Math.toIntExact(deviceNo.getNo()), 822);
                    if (0 == type) result.add(Math.toIntExact(deviceNo.getNo()));
                } catch (Exception ignored) {

                }
            }
        } else if ("Power-cabinets".equals(typeTxt)) {
            for (Temp deviceNo : deviceNos) {
                try {
                    int type = key.getTelecommand(Math.toIntExact(deviceNo.getNo()), 822);
                    // System.out.println(deviceNo.getNo() + ":::==>" + type);
                    if (1 == type) result.add(Math.toIntExact(deviceNo.getNo()));
                    // System.out.println(deviceNo + ":::" + type);
                } catch (Exception ignored) {

                }
            }
        }
        return result;
    }

    // 构建返回给上位机的耗电量数据 类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量
    public List<Long> getRtuData(String type) {
        List<Long> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        switch (type) {
            case "day": {
                map.put("type1", 2);
                map.put("type2", "day");
                break;
            }
            case "week": {
                map.put("type1", 3);
                map.put("type2", "week");
                break;
            }
            case "month": {
                map.put("type1", 4);
                map.put("type2", "month");
                break;
            }
            case "year": {
                map.put("type1", 6);
                map.put("type2", "year");
                break;
            }
        }

        List<Long> devices = getDevicePowerByDistribution((Integer) map.get("type1"));
        List<Long> zones = getZonePowerByDistribution(map.get("type2").toString());

        for (int i = 0; i < 15; i++) {
            if (i < 7) {
                try {
                    result.add(devices.get(i));
                } catch (Exception e) {
                    result.add(0L);
                }
            } else {
                try {
                    result.add(zones.get(i - 7));
                } catch (Exception e) {
                    result.add(0L);
                }
            }
        }

        return result;
    }

    // 获取设备的耗电量 7台电源柜
    private List<Long> getDevicePowerByDistribution(Integer type) {
        List<Long> result = new ArrayList<>();
        List<Integer> deviceNoByType = getDeviceNoByType("Power-cabinets");
        // System.out.println("deviceNoByType::" + deviceNoByType);
        for (Integer no : deviceNoByType) {
            LambdaQueryWrapper<DevBasePower> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DevBasePower::getDeviceId, no);
            lqw.eq(DevBasePower::getType, type);
            lqw.orderByDesc(DevBasePower::getTimestamp);
            DevBasePower power = powerMapper.selectOne(lqw);
            if (power != null && power.getValue() != null) {
                result.add(power.getValue().multiply(BigDecimal.valueOf(100)).longValue());
            } else result.add(0L);
        }
        // System.err.println("result<==:::" + result);
        return result;
    }

    // 获取分区组合的耗电量 1~8
    private List<Long> getZonePowerByDistribution(String type) {
        List<Long> result = new ArrayList<>();
        List<CabinetCalculationData> sourceList = calculationController.getSourceList(type);
        // System.err.println(sourceList);
        sourceList.sort(Comparator.comparingInt(CabinetCalculationData::getId));
        for (CabinetCalculationData data : sourceList) {
            if (data != null && data.getPower() != null) {
                result.add(new BigDecimal(data.getPower()).multiply(BigDecimal.valueOf(100)).longValue());
            } else {
                result.add(0L);
            }
        }
        return result;
    }


    @Data
    public class Temp {
        private Long no;
        private Long ip;
        private Boolean link;
    }
}
