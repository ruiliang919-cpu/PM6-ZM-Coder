package com.ruoyi.zm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevStatusAcLoop;
import com.ruoyi.zm.domain.DevStatusDccLoop;
import com.ruoyi.zm.domain.bo.DevBaseDeviceBo;
import com.ruoyi.zm.domain.vo.DevBaseDeviceVo;
import com.ruoyi.zm.domain.vo.DevBaseRegionVo;
import com.ruoyi.zm.domain.vo.WebLightStatusRespVO;
import com.ruoyi.zm.mapper.DevStatusAcLoopMapper;
import com.ruoyi.zm.mapper.DevStatusDccLoopMapper;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import com.ruoyi.zm.service.IDevBaseRegionService;
import com.ruoyi.zm.service.LightingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LightingServiceImpl implements LightingService {
    @Resource
    private IDevBaseDeviceService iDevBaseDeviceService;
    @Resource
    private DevStatusDccLoopMapper dccLoopMapper;
    @Resource
    private IDevBaseRegionService iDevBaseRegionService;
    @Resource
    private DevStatusAcLoopMapper acLoopMapper;


    @Override
    public TableDataInfo<WebLightStatusRespVO> lightList(PageQuery pageQuery) {
        TableDataInfo<WebLightStatusRespVO> lightPageList = new TableDataInfo<>();
        DevBaseDeviceBo bo = new DevBaseDeviceBo();
        TableDataInfo<DevBaseDeviceVo> devPageList = iDevBaseDeviceService.queryPageList(bo, pageQuery);
        lightPageList.setTotal(devPageList.getTotal());
        List<WebLightStatusRespVO> transformedList = new ArrayList<>();
        long id = 0L;
        for (DevBaseDeviceVo deviceVo : devPageList.getRows()) {
            WebLightStatusRespVO webLightStatusRespVO = new WebLightStatusRespVO();
            webLightStatusRespVO.setId(id++);
            if (deviceVo.getDeviceName() != null) {
                webLightStatusRespVO.setDeviceName(deviceVo.getDeviceName());
            }
            if (deviceVo.getDeviceNo() != null) {
                webLightStatusRespVO.setDeviceNo(deviceVo.getDeviceNo());
            }
            if (deviceVo.getRegionId() != null) {
                DevBaseRegionVo devBaseRegionVo = iDevBaseRegionService.queryById(deviceVo.getRegionId());
                if (devBaseRegionVo != null && devBaseRegionVo.getName() != null) {
                    webLightStatusRespVO.setRegionName(devBaseRegionVo.getName());
                }
            }
            // webLightStatusRespVO.setDcModuleNum(deviceVo.getDcModuleNum());
            // long dccOff = getDccOff(deviceVo.getDeviceNo(), deviceVo.getDimmerNum());
            if (deviceVo.getDimmerNum() != null && deviceVo.getDcSwitchNum() != null && deviceVo.getDeviceNo() != null) {
                long dccOff = getDccOff(deviceVo.getDeviceNo(), deviceVo.getDimmerNum() + deviceVo.getDcSwitchNum());
                // 直流开灯回路数
                webLightStatusRespVO.setDcTurnOnNum(deviceVo.getDimmerNum() + deviceVo.getDcSwitchNum() - dccOff);
                // 直流关灯回路数
                webLightStatusRespVO.setDcTurnOffNum(dccOff);
                webLightStatusRespVO.setDcList(getDccListBySlaveId(deviceVo.getDeviceNo(), deviceVo.getDimmerNum(), deviceVo.getDcSwitchNum()));
                // 直流回路灯总数，而不是DC模块的个数，此处使用DcModuleNum当作总数
                webLightStatusRespVO.setDcModuleNum(deviceVo.getDimmerNum() + deviceVo.getDcSwitchNum());
            }
            if (deviceVo.getAcLoopNum() != null && deviceVo.getDeviceNo() != null) {
                long acOff = getAcOff(deviceVo.getDeviceNo(), deviceVo.getAcLoopNum());
                webLightStatusRespVO.setAcModuleNum(deviceVo.getAcLoopNum());
                // 交流开灯回路数
                webLightStatusRespVO.setAcTurnOnNum(deviceVo.getAcLoopNum() - acOff);
                // 交流关灯回路数
                webLightStatusRespVO.setAcTurnOffNum(acOff);
                webLightStatusRespVO.setAcList(getAcListBySlaveId(deviceVo.getDeviceNo(), deviceVo.getAcLoopNum()));
            }
            transformedList.add(webLightStatusRespVO);
        }
        lightPageList.setRows(transformedList);
        return lightPageList;
    }

    // 获取直流关灯回路数
    private long getDccOff(Long slaveId, Integer limit) {
        LambdaQueryWrapper<DevStatusDccLoop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevStatusDccLoop::getDeviceId, slaveId);
        // lqw.eq(DevStatusDccLoop::getSwitchFeedback, 0);
        lqw.orderByAsc(DevStatusDccLoop::getNo);
        List<DevStatusDccLoop> dccLoops = dccLoopMapper.selectList(lqw);
        if (!dccLoops.isEmpty()) {
            // System.out.println(dccLoops);
            // System.out.println(limit);
            // System.out.println(dccLoops.stream().limit(limit).filter(item -> item.getSwitchFeedback() == 0).count());
            return dccLoops.stream().limit(limit).filter(item -> item.getSwitchFeedback() == 0).count();
        } else {
            return 0;
        }
    }

    // 获取直流状态列表
    private List<DevStatusDccLoop> getDccListBySlaveId(Long slaveId, Integer dimmerNum, Integer switchNum) {
        LambdaQueryWrapper<DevStatusDccLoop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevStatusDccLoop::getDeviceId, slaveId);
        List<DevStatusDccLoop> result = dccLoopMapper.selectList(lqw);
        if (result.size() >= dimmerNum + switchNum) {
            List<DevStatusDccLoop> devStatusDccLoops = result.subList(0, dimmerNum + switchNum);
            for (int i = 0; i < dimmerNum + switchNum; i++) {
                if (i < dimmerNum) {
                    devStatusDccLoops.get(i).setType(1);
                } else {
                    devStatusDccLoops.get(i).setType(2);
                }
            }
            return devStatusDccLoops;
        } else {
            return Collections.emptyList();
        }
    }

    // 获取交流关灯回路数
    private long getAcOff(Long slaveId, Integer limit) {
        LambdaQueryWrapper<DevStatusAcLoop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevStatusAcLoop::getDeviceId, slaveId);
        // lqw.eq(DevStatusAcLoop::getSwitchFeedback, 0);
        lqw.orderByAsc(DevStatusAcLoop::getNo);
        List<DevStatusAcLoop> acLoops = acLoopMapper.selectList(lqw);
        if (!acLoops.isEmpty()) {
            return acLoops.stream().limit(limit).filter(item -> item.getSwitchFeedback() == 0).count();
        } else {
            return 0;
        }
    }

    // 获取交流状态列表
    private List<DevStatusAcLoop> getAcListBySlaveId(Long slaveId, Integer length) {
        LambdaQueryWrapper<DevStatusAcLoop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevStatusAcLoop::getDeviceId, slaveId);
        List<DevStatusAcLoop> result = acLoopMapper.selectList(lqw);
        // System.out.println(result);
        if (result.size() >= length) {
            return result.subList(0, length);
        } else {
            return Collections.emptyList();
        }
    }
}
