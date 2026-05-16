package com.ruoyi.zm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevBaseRegion;
import com.ruoyi.zm.domain.DevConfigDistrict;
import com.ruoyi.zm.domain.bo.BaseDeviceReqBo;
import com.ruoyi.zm.domain.vo.BaseDeviceResp;
import com.ruoyi.zm.domain.vo.ControlPartitionResp;
import com.ruoyi.zm.domain.vo.DevBaseSceneVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseRegionMapper;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import com.ruoyi.zm.mapper.DevConfigDistrictMapper;
import com.ruoyi.zm.service.BasicService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BasicServiceImpl implements BasicService {
    @Resource
    private DevConfigDistrictMapper districtMapper;
    @Resource
    private DevBaseSceneMapper baseSceneMapper;
    @Resource
    private DevBaseDeviceMapper deviceMapper;
    @Resource
    private DevBaseRegionMapper regionMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<ControlPartitionResp> getControlPartitionList() {
        List<ControlPartitionResp> result = new ArrayList<>();
        List<DevConfigDistrict> devConfigDistricts = districtMapper.selectAllDistinctDistrictId();
        devConfigDistricts.forEach(item -> {
            ControlPartitionResp resp = new ControlPartitionResp();
            resp.setDistrictId(Math.toIntExact(item.getDistrictId()));
            resp.setName(item.getName());
            result.add(resp);
        });
        return result;
    }

    @Override
    public List<DevBaseSceneVo> getSceneList() {
        return baseSceneMapper.selectAllDistinctSceneId();
    }

    @Override
    public List<BaseDeviceResp> getCabinetList(PageQuery pageQuery) {
        List<DevBaseDevice> page = deviceMapper.selectList();
        List<BaseDeviceResp> respList = new ArrayList<>();
        page.forEach(item -> {
            BaseDeviceResp resp = new BaseDeviceResp();
            resp.setId(Math.toIntExact(item.getId()));
            resp.setName(item.getDeviceName());
            resp.setDeviceId(Math.toIntExact(item.getDeviceId()));
            resp.setDeviceNo(Math.toIntExact(item.getDeviceNo()));
            resp.setIp(item.getIp());
            resp.setPort(item.getPort());
            try {
                LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
                lqw.eq(DevBaseRegion::getId, item.getRegionId());
                DevBaseRegion region = regionMapper.selectOne(lqw);
                resp.setArea(region.getName());
            } catch (Exception ignored) {
                resp.setArea("未知区域");
                resp.setRunMode(0);
            }
            respList.add(resp);
        });
        return respList;
    }

    // 将运行模式、类型、直流模块个数与调光模块个数写入到设备中
    @Override
    public boolean addCabinet(BaseDeviceReqBo bo) {
        DevBaseDevice device = new DevBaseDevice();
        int insert = deviceMapper.insert(device);
        return insert > 0;
    }


}
