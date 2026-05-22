package com.ruoyi.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseRegion;
import com.ruoyi.zm.domain.DevStatusAcLoop;
import com.ruoyi.zm.domain.DevStatusDccLoop;
import com.ruoyi.zm.domain.vo.WebLightStatusRespVO;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightingCache {
    private final Key key;
    private final DevBaseRegionMapper regionMapper;
    private final DevBaseDeviceMapper deviceMapper;

    public TableDataInfo<WebLightStatusRespVO> getLightList(PageQuery pageQuery) {
        try {
            // 机柜列表
            List<DevBaseDevice> devices = deviceMapper.selectList();
            // 照明列表
            List<WebLightStatusRespVO> transformedList = new ArrayList<>();
            if (!devices.isEmpty()) {
                long id = 0L;
                for (DevBaseDevice device : devices) {
                    try {
                        id++;
                        WebLightStatusRespVO webLightStatusRespVO = new WebLightStatusRespVO();
                        // 照明列表ID
                        webLightStatusRespVO.setId(id);
                        // 设置照明列表设备名称
                        if (device.getDeviceName() != null) webLightStatusRespVO.setDeviceName(device.getDeviceName());
                        // 设置照明列表设备编号
                        if (device.getDeviceNo() != null) webLightStatusRespVO.setDeviceNo(device.getDeviceNo());
                        // 设置照明列表区域名称
                        webLightStatusRespVO.setRegionName(getAreaName(Math.toIntExact(device.getRegionId())));
                        // 直流调光回路数量
                        int dimmerNum = Optional.ofNullable((BigDecimal)
                                key.getTelemeter(Math.toIntExact(device.getDeviceNo()), "0x0022"))
                            .map(BigDecimal::intValue).orElse(0);
                        // 直流开关回路数量
                        int dcSwitchNum = Optional.ofNullable(((BigDecimal)
                                key.getTelemeter(Math.toIntExact(device.getDeviceNo()), "0x0023")))
                            .map(BigDecimal::intValue).orElse(0);
                        // 直流回路反馈开关状态数组
                        int[] dccSwitchArr = key.getTelecommandByIntArr(Math.toIntExact(device.getDeviceNo()), 474, 513);
                        // 直流回路关灯数量
                        long dccOff = getOff(dimmerNum + dcSwitchNum, dccSwitchArr);
                        // 设置直流开灯回路数
                        webLightStatusRespVO.setDcTurnOnNum(dimmerNum + dcSwitchNum - dccOff);
                        // 设置直流关灯回路数
                        webLightStatusRespVO.setDcTurnOffNum(dccOff);
                        // 设置直流回路灯总数，而不是DC模块的个数，此处使用DcModuleNum当作总数
                        webLightStatusRespVO.setDcModuleNum(dimmerNum + dcSwitchNum);
                        // 设置直流信息列表
                        webLightStatusRespVO.setDcList(getDccListBySlaveId(device.getDeviceNo(), dimmerNum, dcSwitchNum, dccSwitchArr));
                        // 交流回路数量
                        int acLoopNum = 0;
                        Object remote = key.getRemote(Math.toIntExact(device.getDeviceNo()), "0XAF1Enum");
                        if (remote != null) acLoopNum = Integer.parseInt(remote.toString());


                        // 交流回路反馈开关状态数组
                        int[] acSwitchArr = key.getTelecommandByIntArr(Math.toIntExact(device.getDeviceNo()), 522, 529);
                        // 交流关灯回路
                        long acOff = getOff(acLoopNum, acSwitchArr);
                        // 设置交流回路数量
                        webLightStatusRespVO.setAcModuleNum(acLoopNum);
                        // 设置交流开灯回路数
                        webLightStatusRespVO.setAcTurnOnNum(acLoopNum - acOff);
                        // 设置交流关灯回路数
                        webLightStatusRespVO.setAcTurnOffNum(acOff);
                        // 设置交流信息列表
                        webLightStatusRespVO.setAcList(getAcListBySlaveId(device.getDeviceNo(), acLoopNum, acSwitchArr));
                        // 注入对象，配电柜才显示
                        if (0 == key.getTelecommand(Math.toIntExact(device.getDeviceNo()), 822)) {
                            //                        System.err.println(webLightStatusRespVO);
                            transformedList.add(webLightStatusRespVO);
                        }
                    } catch (Exception e) {
                        //                        log.error("LightingCache → getLightList", e);
                    }
                    //                    System.out.println(transformedList);
                }
                return key.getPageTable(transformedList, pageQuery);
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    // 获取直流/交流关灯回路数
    private long getOff(Integer limit, int[] arr) {
        List<Integer> loops = Arrays.stream(arr).boxed().collect(Collectors.toList());
        if (!loops.isEmpty()) return loops.stream().limit(limit).filter(item -> item == 0).count();
        else return 0;
    }

    // 获取直流状态列表
    private List<DevStatusDccLoop> getDccListBySlaveId(Long slaveId, Integer dimmerNum, Integer switchNum, int[] dccSwitchArr) {
        List<DevStatusDccLoop> result = new ArrayList<>();
        // 直流回路反馈亮度列表
        List<BigDecimal> brightnessFeedbackList = key.getTelemeterBigDecimalList(Math.toIntExact(slaveId), "0X04A0");
        if (brightnessFeedbackList != null && !brightnessFeedbackList.isEmpty()) {
            for (int i = 0; i < dimmerNum + switchNum; i++) {
                DevStatusDccLoop loop = new DevStatusDccLoop();
                loop.setId((long) (i + 1));
                loop.setNo(String.valueOf(i + 1));
                loop.setDeviceId(slaveId);
                loop.setBrightnessFeedback((long) brightnessFeedbackList.get(i).intValue());
                loop.setSwitchFeedback(dccSwitchArr[i]);
                if (i < dimmerNum) loop.setType(1);
                else loop.setType(2);
                result.add(loop);
            }
        }
        return result;
    }

    // 获取交流状态列表
    private List<DevStatusAcLoop> getAcListBySlaveId(Long slaveId, Integer length, int[] acSwitchArr) {
        List<DevStatusAcLoop> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            DevStatusAcLoop loop = new DevStatusAcLoop();
            loop.setId((long) i + 1);
            loop.setNo(String.valueOf(i + 1));
            loop.setDeviceId(slaveId);
            loop.setSwitchFeedback(acSwitchArr[i]);
            result.add(loop);
        }
        return result;
    }

    // 获取区域名称
    public String getAreaName(Integer areaId) {
        if (areaId == 0 || areaId == null) {
            return "";
        }
        LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseRegion::getId, areaId);
        DevBaseRegion region = this.regionMapper.selectOne(lqw);
        if (region == null) {
            return "";
        }
        return region.getName();
    }
}
