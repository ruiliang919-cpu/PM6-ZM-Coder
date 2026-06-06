package com.ruoyi.zm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.bo.DevBaseDeviceBo;
import com.ruoyi.zm.domain.vo.DevBaseDeviceVo;
import com.ruoyi.zm.domain.vo.DeviceHomeVoResp;

import java.util.List;

/**
 * 机柜Service接口
 *
 * @author mophi
 * @date 2024-07-11
 */
public interface IDevBaseDeviceService {

    /**
     * 查询机柜
     */
    DevBaseDeviceVo queryById(Long id);

    /**
     * 查询机柜列表
     */
    TableDataInfo<DevBaseDeviceVo> queryPageList(DevBaseDeviceBo bo, PageQuery pageQuery);

    /**
     * 查询机柜列表
     */
    List<DevBaseDeviceVo> queryList(DevBaseDeviceBo bo);

    /**
     * 新增机柜
     */
    Boolean insertByBo(DevBaseDeviceBo bo);

    /**
     * 修改机柜
     */
    Boolean updateByBo(DevBaseDeviceBo bo);

    /**
     * 校验并批量删除机柜信息
     */
    Boolean deleteWithValidByIds(Long ids, Boolean isValid);

    /**
     * 从缓存Redis中查询机柜列表
     */
    List<DevBaseDevice> getListFromCache();

    /**
     * 从缓存中获取单个机柜信息
     */
    @Deprecated
    DevBaseDevice getOneByCache(int slaveId);

    /**
     * 查询机柜信息，用于首页展示
     */
    TableDataInfo<DeviceHomeVoResp> getCabinetList(PageQuery pageQuery);

    /**
     * 根据ID更新设备
     */
    int updateById(DevBaseDevice device);

    /**
     * 查询全部设备列表（无分页）
     */
    List<DevBaseDevice> listAll();

    /**
     * 根据条件查询单个设备
     */
    DevBaseDevice selectOne(LambdaQueryWrapper<DevBaseDevice> lqw);
}
