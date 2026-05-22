package com.ruoyi.cache;

import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.vo.WebDccLoopGroupRespVO;
import com.ruoyi.zm.domain.vo.WebDccLoopGruopReqVO;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoneUtilCache {
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LoopByGroupCache loopByGroupCache;
    private final DevBaseDeviceMapper deviceMapper;
    private final DListUtil dListUtil;

    // 获取交流回路的开关状态
    public List<Boolean> getAcLoopStatus(List<Long> deviceNos) {
        List<Boolean> result = new ArrayList<>();
        for (Long deviceNo : deviceNos) {
            int acNum = 0;
            Object remote =  key.getRemote(Math.toIntExact(deviceNo), "0XAF1Enum");
            if (remote != null) acNum = Integer.parseInt(remote.toString());
            if (acNum > 0) {
                boolean[] acStatus = key.getTelecommand(Math.toIntExact(deviceNo), 514, 514 + acNum - 1);
                if (acStatus != null) for (boolean status : acStatus) result.add(status);
            }
        }
        return result;
    }

    public List<RespData> setData(List<Integer> zoneIds) {
        return processData(zoneIds);
    }

    // 获取直流回路的开关状态
    private List<RespData> processData(List<Integer> zoneIds) {
        List<Integer> deviceNos = getDeviceNos().stream().sorted().collect(Collectors.toList());
        List<RespData> result = new ArrayList<>();
        try {
            result = deviceNos.stream()
                .flatMap(deviceNo -> getGroupByZone(deviceNo, zoneIds).stream()
                    .flatMap(groupId -> getLoopByGroup(deviceNo, groupId).stream()
                        .flatMap(webDccLoopGroupRespVO -> getStatusByLoop(deviceNo, webDccLoopGroupRespVO.getNo()).stream()
                            .map(status -> {
                                RespData respData = new RespData();
                                respData.setDeviceId(Long.valueOf(deviceNo));
                                try {
                                    respData.setLoopNo(Long.valueOf(webDccLoopGroupRespVO.getNo()));
                                    respData.setLoopStatus(status);
                                } catch (Exception e) {
                                    log.error("ZoneUtilCache → processData 报错？", e);
                                }
                                return respData;
                            })
                        )
                    )
                ).distinct()
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("ZoneUtilCache → processData", e);
        }
        return result;
    }

    // 根据IP地址的大小获取所有在线的设备的设备号
    public List<Integer> getDeviceNos() {
        List<DevBaseDevice> devices = dListUtil.List();
        List<Integer> result = new ArrayList<>();
        if (!devices.isEmpty()) {
            List<Temp> temps = new ArrayList<>();
            for (DevBaseDevice device : devices) {
                try {
                    Temp temp = new Temp();
                    temp.setNo(device.getDeviceNo());
                    String[] IpArr = device.getIp().split("\\.");
                    temp.setIp(Long.parseLong(IpArr[0] + IpArr[1] + IpArr[2] + IpArr[3]));
                    temps.add(temp);
                } catch (Exception e) {
                    log.error("ZoneUtilCache → getDeviceNos 报错？", e);
                }
            }
            temps.sort(Comparator.comparingLong(Temp::getIp));
            for (Temp item : temps) {
                // DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(Math.toIntExact(item.getNo()));
                // if (tcpVo != null && tcpVo.getOnlineStatus() != null && tcpVo.getOnlineStatus() == 1 && tcpVo.getId() != null) {
                // result.add(Math.toIntExact(tcpVo.getId()));
                result.add(Math.toIntExact(item.getNo()));
                // }
            }
        }
        return result;
    }

    // 根据分区ID获取分组
    public List<Integer> getGroupByZone(Integer deviceId, List<Integer> zoneId) {
        zoneId = zoneId.stream().sorted().collect(Collectors.toList());

        short[] codes = key.getRemoteByArr(deviceId, "0xA5AE");
        List<Integer> result = new ArrayList<>();

        if (codes != null && codes.length > 0) {
            // 创建一个映射，将 zoneId 映射到其对应的分组
            Map<Integer, Integer> zoneToGroupMap = new HashMap<>();
            for (int i = 0; i < codes.length; i++) {
                zoneToGroupMap.put(i + 1, (int) codes[i]);
            }

            // 按照 zoneId 的顺序添加到 result 列表中
            List<Integer> finalZoneId = zoneId;
            // System.out.println("zoneToGroupMap: " + zoneToGroupMap);
            zoneToGroupMap.forEach((key, value) -> {
                if (finalZoneId.contains(value)) result.add(key);
            });
        }

        return result;
    }

    // 根据分组获取回路
    private List<WebDccLoopGroupRespVO> getLoopByGroup(Integer deviceId, Integer groupId) {
        WebDccLoopGruopReqVO vo = new WebDccLoopGruopReqVO();
        vo.setSlaveId(Long.valueOf(deviceId));
        vo.setGroupId(Long.valueOf(groupId));
        // System.out.println("deviceId::" + deviceId + ":::" + groupId + ":::" + loopByGroupCache.getLoopsByGroup(vo).getData());
        return loopByGroupCache.getLoopsByGroup(vo).getData();
    }

    // 根据回路获取回路开关状态 0-关 1-开
    private List<Integer> getStatusByLoop(Integer deviceId, Integer loopNo) {
        boolean[] statusArr = key.getTelecommand(deviceId, 474, 513);
        List<Integer> result = new ArrayList<>();
        if (statusArr == null || statusArr.length < 40) {
            statusArr = new boolean[40];
        }
        for (int i = 0; i < statusArr.length; i++) {
            if (loopNo == (i + 1)) {
                result.add(statusArr[i] ? 1 : 0);
            }
        }
        return result;
    }

    @Data
    public static class Temp {
        private Long no;
        private Long ip;
    }

    @Data
    public static class RespData {
        private Long deviceId;
        private Long loopNo;
        private Integer loopStatus;
    }
}
