package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseRegion;
import com.ruoyi.zm.domain.DevDeviceRemoteRead;
import com.ruoyi.zm.domain.bo.DevBaseDeviceBo;
import com.ruoyi.zm.domain.vo.DevBaseDeviceVo;
import com.ruoyi.zm.domain.vo.DeviceHomeVoResp;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import com.ruoyi.zm.mapper.DevDeviceRemoteReadMapper;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 机柜Service业务层处理
 *
 * @author mophi
 * @date 2024-07-11
 */
@RequiredArgsConstructor
@Service
public class DevBaseDeviceServiceImpl implements IDevBaseDeviceService {

    private final DevBaseDeviceMapper baseMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询机柜
     */
    @Override
    public DevBaseDeviceVo queryById(Long id) {
        return baseMapper.selectVoById(id);
        // return  null;
    }

    /**
     * 查询机柜列表
     */
    @Override
    public TableDataInfo<DevBaseDeviceVo> queryPageList(DevBaseDeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseDevice> lqw = buildQueryWrapper(bo);
        Page<DevBaseDeviceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询机柜列表
     */
    @Override
    public List<DevBaseDeviceVo> queryList(DevBaseDeviceBo bo) {
        LambdaQueryWrapper<DevBaseDevice> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseDevice> buildQueryWrapper(DevBaseDeviceBo bo) {
        LambdaQueryWrapper<DevBaseDevice> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), DevBaseDevice::getDeviceName, bo.getDeviceName());
        lqw.eq(bo.getDeviceNo() != null, DevBaseDevice::getDeviceNo, bo.getDeviceNo());
        lqw.eq(bo.getDeviceId() != null, DevBaseDevice::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getRegionId() != null, DevBaseDevice::getRegionId, bo.getRegionId());
        lqw.eq(bo.getOnlineStatus() != null, DevBaseDevice::getOnlineStatus, bo.getOnlineStatus());
        lqw.eq(bo.getDeviceStatus() != null, DevBaseDevice::getDeviceStatus, bo.getDeviceStatus());
        lqw.eq(bo.getRunStatus() != null, DevBaseDevice::getRunStatus, bo.getRunStatus());
        lqw.eq(bo.getRunMode() != null, DevBaseDevice::getRunMode, bo.getRunMode());
        lqw.eq(bo.getRunModeType() != null, DevBaseDevice::getRunModeType, bo.getRunModeType());
        lqw.eq(bo.getTemperature() != null, DevBaseDevice::getTemperature, bo.getTemperature());
        lqw.eq(bo.getAcInputLoop() != null, DevBaseDevice::getAcInputLoop, bo.getAcInputLoop());
        lqw.eq(bo.getAcCurrentDisplay() != null, DevBaseDevice::getAcCurrentDisplay, bo.getAcCurrentDisplay());
        lqw.eq(bo.getAcSamplingMode() != null, DevBaseDevice::getAcSamplingMode, bo.getAcSamplingMode());
        lqw.eq(bo.getDcModuleNum() != null, DevBaseDevice::getDcModuleNum, bo.getDcModuleNum());
        lqw.eq(bo.getAcLoopNum() != null, DevBaseDevice::getAcLoopNum, bo.getAcLoopNum());
        lqw.eq(bo.getOtherSwitchNum() != null, DevBaseDevice::getOtherSwitchNum, bo.getOtherSwitchNum());
        lqw.eq(bo.getAcModuleNum() != null, DevBaseDevice::getAcModuleNum, bo.getAcModuleNum());
        lqw.eq(bo.getDimmerNum() != null, DevBaseDevice::getDimmerNum, bo.getDimmerNum());
        lqw.eq(bo.getDcSwitchNum() != null, DevBaseDevice::getDcSwitchNum, bo.getDcSwitchNum());
        lqw.eq(bo.getDcBusVoltage() != null, DevBaseDevice::getDcBusVoltage, bo.getDcBusVoltage());
        lqw.eq(bo.getDcBusCurrent() != null, DevBaseDevice::getDcBusCurrent, bo.getDcBusCurrent());
        lqw.eq(bo.getBusDirectVoltageToEarth() != null, DevBaseDevice::getBusDirectVoltageToEarth, bo.getBusDirectVoltageToEarth());
        lqw.eq(bo.getBusNegativeVoltageToEarth() != null, DevBaseDevice::getBusNegativeVoltageToEarth, bo.getBusNegativeVoltageToEarth());
        lqw.eq(bo.getPositivePoleResistance() != null, DevBaseDevice::getPositivePoleResistance, bo.getPositivePoleResistance());
        lqw.eq(bo.getNegativePoleResistance() != null, DevBaseDevice::getNegativePoleResistance, bo.getNegativePoleResistance());
        lqw.eq(bo.getBusbarCrossoverVoltage() != null, DevBaseDevice::getBusbarCrossoverVoltage, bo.getBusbarCrossoverVoltage());
        lqw.eq(bo.getBusbarCrossoverCurrent() != null, DevBaseDevice::getBusbarCrossoverCurrent, bo.getBusbarCrossoverCurrent());
        lqw.eq(bo.getLastTime() != null, DevBaseDevice::getLastTime, bo.getLastTime());
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), DevBaseDevice::getIp, bo.getIp());
        lqw.eq(bo.getPort() != null, DevBaseDevice::getPort, bo.getPort());
        lqw.eq(bo.getType() != null, DevBaseDevice::getType, bo.getType());
        lqw.eq(bo.getAcDcOutputVoltage() != null, DevBaseDevice::getAcDcOutputVoltage, bo.getAcDcOutputVoltage());
        lqw.eq(bo.getDcDcOutputVoltage() != null, DevBaseDevice::getDcDcOutputVoltage, bo.getDcDcOutputVoltage());
        lqw.eq(bo.getSceneSelect() != null, DevBaseDevice::getSceneSelect, bo.getSceneSelect());
        return lqw;
    }

    /**
     * 新增机柜 // TODO：将直流模块个数与调光模块个数写入到设备中
     */
    @Override
    public Boolean insertByBo(DevBaseDeviceBo bo) {
        DevBaseDevice device = new DevBaseDevice();
        // device.setId(bo.getDeviceNo());
        device.setDeviceName(bo.getDeviceName());
        device.setRegionId(bo.getRegionId());
        device.setIp(bo.getIp());
        device.setPort(Math.toIntExact(bo.getPort()));
        device.setDeviceId(Math.toIntExact(bo.getDeviceNo()));
        device.setRunMode(bo.getRunMode());
        device.setDcModuleNum(Math.toIntExact(bo.getDcModuleNum()));
        device.setDimmerNum(Math.toIntExact(bo.getDimmerNum()));
        device.setType(bo.getType());
        device.setDelFlag("0");
        int insert = baseMapper.insert(device);
        if (insert > 0) {
            // 将自动生成的ID回写到deviceNo字段，使用update而非insert避免重复数据
            device.setDeviceNo(device.getId());
            insert = baseMapper.updateById(device);
        }
        return insert > 0;
    }

    /**
     * 修改机柜 // TODO：将直流模块个数与调光模块个数写入到设备中
     */
    @Override
    public Boolean updateByBo(DevBaseDeviceBo bo) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, bo.getDeviceNo());
        List<DevBaseDevice> devBaseDevices = baseMapper.selectList(lqw);
        String ip = bo.getIp();
        long otherDevicesCount = devBaseDevices.stream()
            .filter(device -> !device.getIp().equals(ip))
            .count();
        if (otherDevicesCount > 0) {
            return false;
        }
        DevBaseDevice device = new DevBaseDevice();
        device.setId(bo.getId());
        device.setDeviceName(bo.getDeviceName());
        device.setRegionId(bo.getRegionId());
        device.setIp(bo.getIp());
        device.setDeviceId(Math.toIntExact(bo.getDeviceNo()));
        device.setRunMode(bo.getRunMode());
        device.setDcModuleNum(Math.toIntExact(bo.getDcModuleNum()));
        device.setDimmerNum(Math.toIntExact(bo.getDimmerNum()));
        int update = baseMapper.updateById(device);
        return update > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseDevice entity) {
    }

    /**
     * 批量删除机柜
     */
    @Override
    public Boolean deleteWithValidByIds(Long ids, Boolean isValid) {
        redisTemplate.delete("zm:create-tcp:" + ids);
        redisTemplate.delete("zm:dev_base_devices:list");
        return baseMapper.deleteReality(ids) > 0;
    }

    private volatile List<DevBaseDevice> deviceList = null;
    private final Object deviceListLock = new Object();

    @Override
    public List<DevBaseDevice> getListFromCache() {
        List<DevBaseDevice> result = deviceList;
        if (ObjectUtils.isEmpty(result)) {
            synchronized (deviceListLock) {
                result = deviceList;
                if (ObjectUtils.isEmpty(result)) {
                    result = baseMapper.selectList();
                    deviceList = result;
                }
            }
        }
        return result;
    }

    // 获取单个机柜
    @Deprecated
    public DevBaseDevice getOneByCache(int slaveId) {
        DevBaseDevice modbusInfo = (DevBaseDevice) redisTemplate.opsForValue().get("zm:one-device:" + slaveId);
        // System.out.println("modbusInfo:"+modbusInfo);
        if (modbusInfo == null) {
            LambdaQueryWrapper<DevBaseDevice> queryWrapper = new LambdaQueryWrapper<>();
            modbusInfo = baseMapper.selectOne(queryWrapper.eq(DevBaseDevice::getDeviceNo, slaveId));
            redisTemplate.opsForValue().set("zm:one-device:" + slaveId, modbusInfo, 1, TimeUnit.DAYS);
        }
        return modbusInfo;
    }


    private final DevDeviceRemoteReadMapper remoteReadMapper;
    private final DevBaseRegionMapper regionMapper;


    @Override
    public TableDataInfo<DeviceHomeVoResp> getCabinetList(PageQuery pageQuery) {
        Page<DevBaseDeviceVo> devBaseDeviceVoIPage = baseMapper.selectVoPage(pageQuery.build(), new QueryWrapper<>());
        List<DeviceHomeVoResp> resultList = new ArrayList<>();
        // System.out.println(devBaseDeviceVoIPage);
        AtomicInteger id = new AtomicInteger(1);
        devBaseDeviceVoIPage.getRecords().forEach(item -> {
            DeviceHomeVoResp resp = new DeviceHomeVoResp();
            BeanUtil.copyProperties(item, resp);
            resp.setDeviceId(Math.toIntExact(item.getDeviceNo()));
            resp.setOrderNo(id.getAndIncrement());
            LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DevBaseRegion::getId, item.getRegionId());
            DevBaseRegion region = regionMapper.selectOne(lqw);
            resp.setArea(region != null ? region.getName() : "未知区域");
            LambdaQueryWrapper<DevDeviceRemoteRead> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(DevDeviceRemoteRead::getDeviceId, item.getDeviceNo());
            DevDeviceRemoteRead devDeviceRemoteRead = remoteReadMapper.selectOne(lqw1);
            if (devDeviceRemoteRead != null && devDeviceRemoteRead.getAcSwitchNum() != null) {
                resp.setAcSwitchNum(devDeviceRemoteRead.getAcSwitchNum());
            }
            resultList.add(resp);
        });
        return TableDataInfo.build(resultList);
    }

    @Override
    public int updateById(DevBaseDevice device) {
        return baseMapper.updateById(device);
    }

    @Override
    public List<DevBaseDevice> listAll() {
        return baseMapper.selectList();
    }

    @Override
    public DevBaseDevice selectOne(LambdaQueryWrapper<DevBaseDevice> lqw) {
        return baseMapper.selectOne(lqw);
    }

}
