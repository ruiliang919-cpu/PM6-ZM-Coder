package com.ruoyi.netty.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.ZoneUtilCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.modbus.util.RecordPlus;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqttwrite.loopcontrol.LoopControlVO;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.web.controller.zm.ACSController;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.DevLightZoneCombination;
import com.ruoyi.zm.domain.vo.WriteGroupReqVo;
import com.ruoyi.zm.domain.vo.WriteLoopReqVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.mapper.DevLightZoneCombinationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RtuWriteUtil {
    private final DevBaseDeviceMapper deviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZoneUtilCache zoneUtilCache;
    private final RecordPlus record;
    private final WriteController writeController;
    private final DevLightZoneCombinationMapper lightZoneCombinationMapper;
    private final DevBaseDistrictMapper districtMapper;
    private final ACSController acsController;
    private final DListUtil dListUtil;
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    private final MqttPublisher mqttPublisher;
    private final RedisTemplate<String, int[]> intArrayRedisTemplate;
    private final Key k;

    // 上位机进入场景
    public void intoScenes(Integer sceneId) {
        // 接收到指令前，主机应先记录所有机柜当前手、自动状态
        record.runModule();
        List<Long> nos = dListUtil.Nos();
        if (nos != null && !nos.isEmpty()) {
            nos.forEach(no -> {
                // 把当前自动状态下的机柜，切换到手动状态，并手动场景控制该机柜进入场景
                writeController.intoScene(Math.toIntExact(no), sceneId);
            });
        }
        redisTemplate.opsForValue().set("zm:global:scene:select", sceneId + "");
        redisTemplate.opsForValue().set("zm:global:scene:status", "");
    }

    // 上位机开关灯 Modbus 从站版本
    public void loopControl(short data, int type) {
        loopControlFun(data, type, null);
    }

    public void loopControlFun(short data, int type, Integer lux) {
        List<Integer> zoneIds = new ArrayList<>();
        DevLightZoneCombination source = lightZoneCombinationMapper.selectOne(new LambdaQueryWrapper<DevLightZoneCombination>()
            .eq(DevLightZoneCombination::getZoneId, type));
        if (source != null && source.getZoneList() != null && !source.getZoneList().isEmpty()) {
            String[] split = source.getZoneList().split(",");
            for (String zoneId : split) {
                zoneIds.add(Integer.parseInt(zoneId));
            }
        }
        if (zoneIds.isEmpty()) return;

        if (lux != null) {
            // 接收到指令前，主机应先记录所有机柜当前手、自动状态
            record.runModule();
            zoneControlFun(1, zoneIds, lux);
        } else {
            String binaryString = String.format("%16s", Integer.toBinaryString(data)).replace(' ', '0');
            binaryString = new StringBuilder(binaryString).reverse().toString();
            String[] arr = binaryString.split("");
            // 开灯
            if (1 == Integer.parseInt(arr[0])) {
                // 接收到指令前，主机应先记录所有机柜当前手、自动状态
                record.runModule();
                zoneControl(1, zoneIds);
            }
            // 关灯
            if (1 == Integer.parseInt(arr[1])) {
                record.runModule();
                zoneControl(0, zoneIds);
            }
        }
    }

    private final ExecutorService pool = Executors.newFixedThreadPool(15);

    // 分区开关灯
    private void zoneControl(int swStatus, List<Integer> zoneIds) {
        zoneControlFun(swStatus, zoneIds, null);
    }

    private void zoneControlFun(int swStatus, List<Integer> zoneIds, Integer lux) {
        LambdaQueryWrapper<DevBaseDistrict> lqw = new LambdaQueryWrapper<>();
        lqw.in(DevBaseDistrict::getId, zoneIds);
        List<DevBaseDistrict> districts = districtMapper.selectList(lqw);
        districts.forEach(it -> it.setSwitchStatus(swStatus));
        //        log.info("上位机分区控制开关灯状态：{}", districts);
        districtMapper.updateBatchById(districts);

        for (DevBaseDevice device : deviceMapper.selectList()) {
            pool.submit(() -> {
                List<Integer> zoneList = zoneUtilCache.getGroupByZone(Math.toIntExact(device.getDeviceNo()), zoneIds);
                // log.error("{} {}", device.getDeviceNo(), zoneList);
                // 把当前自动状态下的机柜，切换到手动状态，并手动分组控制该机柜进入场景
                if (k.getTelecommand(Math.toIntExact(device.getDeviceNo()), 822) == 0)
                    writeController.workModule(Math.toIntExact(device.getDeviceNo()), 1);
                writeController.selectHandModule(Math.toIntExact(device.getDeviceNo()), 2);
                WriteGroupReqVo vo = new WriteGroupReqVo();
                vo.setDeviceId(Math.toIntExact(device.getDeviceNo()));
                vo.setGroupSelectArr(zoneList.toArray(new Integer[0]));
                if (lux != null) {
                    vo.setLux(lux);
                    writeController.groupControlLux(vo);
                } else {
                    vo.setSwitchStatus(swStatus);
                    writeController.groupControlSwitch(vo);
                }
            });
        }

        redisTemplate.opsForValue().set("zm:global:zone:status", "");
    }

    // 交流开关灯前置方法
    public void acLoopControl(short data) {
        String binaryString = String.format("%16s", Integer.toBinaryString(data)).replace(' ', '0');
        binaryString = new StringBuilder(binaryString).reverse().toString();
        String[] arr = binaryString.split("");
        // 开灯
        if (1 == Integer.parseInt(arr[0])) {
            // 接收到指令前，主机应先记录所有机柜当前手、自动状态
            record.runModule();
            acLoopControl1((short) 1);
        }
        // 关灯
        if (1 == Integer.parseInt(arr[1])) {
            record.runModule();
            acLoopControl1((short) 0);
        }
    }

    // 交流开关灯方法
    private void acLoopControl1(short swStatus) {
        log.info("上位机交流控制开关灯状态：{}", swStatus);
        List<Long> devBaseDevices = dListUtil.Nos();
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            devBaseDevices.forEach(no -> fixedThreadPool.submit(() -> {
                // 把当前自动状态下的机柜，切换到手动状态，并手动回路控制
                if (k.getTelecommand(Math.toIntExact(no), 164) == 0)
                    writeController.workModule(Math.toIntExact(no), 1);
                writeController.selectHandModule(Math.toIntExact(no), 1);
                // 开灯
                if (1 == swStatus) acsController.AllOn(Math.toIntExact(no));
                    // 关灯
                else if (0 == swStatus) acsController.AllOff(Math.toIntExact(no));
            }));
        }
    }

    // 上位机开关灯 Netty 版本
    @Deprecated
    public void loopControl(String data, int zoneId1, int zoneId2) {
    }

    // 回路全亮全灭/亮度调节
    public void allLoopControl(short data) {
        String binaryString = String.format("%16s", Integer.toBinaryString(data)).replace(' ', '0');
        binaryString = new StringBuilder(binaryString).reverse().toString();
        String[] arr = binaryString.split("");
        // 开灯
        if (1 == Integer.parseInt(arr[0])) {
            // 接收到指令前，主机应先记录所有机柜当前手、自动状态
            record.runModule();
            allLoopControl1(1);
            redisTemplate.opsForValue().set("zm:global:loop:status", "true");
            redisTemplate.opsForValue().set("zm:global:loop:status:change", "");
        }
        // 关灯
        if (1 == Integer.parseInt(arr[1])) {
            record.runModule();
            allLoopControl1(0);
            redisTemplate.opsForValue().set("zm:global:loop:status", "false");
            redisTemplate.opsForValue().set("zm:global:loop:status:change", "");
        }
    }

    private static final List<Integer> LOOP_SELECT = new ArrayList<>();

    static {
        for (int i = 0; i < 40; i++) LOOP_SELECT.add((i + 1));
    }

    public void allLoopControl1(int swStatus) {
        List<Long> nos = dListUtil.Nos();
        if (nos != null && !nos.isEmpty()) {
            nos.forEach(deviceNo -> {
                // 把当前自动状态下的机柜，切换到手动状态，并手动回路控制
                if (k.getTelecommand(Math.toIntExact(deviceNo), 164) == 0)
                    writeController.workModule(Math.toIntExact(deviceNo), 1);
                writeController.selectHandModule(Math.toIntExact(deviceNo), 1);
                WriteLoopReqVo vo = new WriteLoopReqVo();
                vo.setDeviceId(Math.toIntExact(deviceNo));
                if (1 == swStatus) {
                    // 开灯
                    vo.setLux(100);
                    vo.setSwitchStatus(swStatus);
                    vo.setLoopControl(LOOP_SELECT);
                    loopC(vo);
                } else if (0 == swStatus) {
                    // 关灯
                    vo.setLux(0);
                    vo.setSwitchStatus(swStatus);
                    vo.setLoopControl(LOOP_SELECT);
                    loopC(vo);
                }
            });
        }
    }

    public R<?> loopC(@RequestBody WriteLoopReqVo reqVo) {
        LoopControlVO v = new LoopControlVO();
        v.setModule(3);
        LoopControlVO.D d = new LoopControlVO.D();
        d.setValue(reqVo.getSwitchStatus());
        d.setLux(reqVo.getLux());

        // 处理回路选中数组
        int[] loopSelectArr = new int[40];
        for (int i = 0; i < reqVo.getLoopControl().size(); i++) {
            loopSelectArr[i] = reqVo.getLoopControl().get(i);
        }
        d.setLoopArr(loopSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.回路总开关, v);

        intArrayRedisTemplate.opsForValue().set("zm:select:loop:" + reqVo.getDeviceId(), loopSelectArr);

        return R.ok("指令下发成功");
    }

}
