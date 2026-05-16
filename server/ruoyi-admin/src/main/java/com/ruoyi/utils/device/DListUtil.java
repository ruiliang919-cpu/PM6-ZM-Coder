package com.ruoyi.utils.device;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.flag.DeviceFlag;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DListUtil {
    private final DevBaseDeviceMapper deviceMapper;
    private List<DevBaseDevice> l = new ArrayList<>();
    private List<Long> no = new ArrayList<>();
    private Map<Long, String> nameMap = new HashMap<>();
    private Map<String, Long> ipMap = new HashMap<>();

    public List<DevBaseDevice> List() {
        if (ObjectUtils.isEmpty(l) || DeviceFlag.Flag(DeviceFlag.LIST)) {
            List<DevBaseDevice> devices = deviceMapper.selectList();
            if (!ObjectUtils.isEmpty(devices)) {
                l = devices;
                DeviceFlag.SetFlag(DeviceFlag.LIST, false);
                // log.info("已重置机柜数据列表");
            }
        }
        return l;
    }

    public List<Long> Nos() {
        if (ObjectUtils.isEmpty(no) || DeviceFlag.Flag(DeviceFlag.NO)) {
            List<DevBaseDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getDeviceNo));
            if (!ObjectUtils.isEmpty(devices)) {
                no = devices.stream().map(DevBaseDevice::getDeviceNo).collect(Collectors.toList());
                DeviceFlag.SetFlag(DeviceFlag.NO, false);
                // log.info("已重置机柜编号列表");
            }
        }
        return no;
    }

    public Map<Long, String> NameMap() {
        if (ObjectUtils.isEmpty(nameMap) || DeviceFlag.Flag(DeviceFlag.NAME)) {
            List<DevBaseDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getDeviceNo, DevBaseDevice::getDeviceName));
            if (!ObjectUtils.isEmpty(devices)) {
                nameMap = devices.stream().collect(Collectors.toMap(DevBaseDevice::getDeviceNo, DevBaseDevice::getDeviceName, (x, y) -> y));
                DeviceFlag.SetFlag(DeviceFlag.NAME, false);
                // log.info("已重置机柜名称映射");
            }
        }
        return nameMap;
    }

    public Map<String, Long> ipMapX() {
        if (ObjectUtils.isEmpty(ipMap) || DeviceFlag.Flag(DeviceFlag.IP)) {
            List<DevBaseDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getIp, DevBaseDevice::getDeviceNo));
            if (!ObjectUtils.isEmpty(devices)) {
                ipMap = devices.stream().collect(Collectors
                    .toMap(d -> d.getIp().split("\\.")[3],
                        DevBaseDevice::getDeviceNo, (x, y) -> y));
                DeviceFlag.SetFlag(DeviceFlag.IP, false);
                log.info("已重置机柜 IP 映射");
            }
        }
        return ipMap;
    }
}
