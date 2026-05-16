package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevEnergyMeterMonthBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterMonthVo;

import java.util.Collection;
import java.util.List;

/**
 * 十六电月耗电量Service接口
 *
 * @author ruoyi
 * @date 2024-09-12
 */
public interface IDevEnergyMeterMonthService {

    /**
     * 查询十六电月耗电量
     */
    DevEnergyMeterMonthVo queryById(Integer id);

    /**
     * 查询十六电月耗电量列表
     */
    TableDataInfo<DevEnergyMeterMonthVo> queryPageList(DevEnergyMeterMonthBo bo, PageQuery pageQuery);

    /**
     * 查询十六电月耗电量列表
     */
    List<DevEnergyMeterMonthVo> queryList(DevEnergyMeterMonthBo bo);

    /**
     * 新增十六电月耗电量
     */
    Boolean insertByBo(DevEnergyMeterMonthBo bo);

    /**
     * 修改十六电月耗电量
     */
    Boolean updateByBo(DevEnergyMeterMonthBo bo);

    /**
     * 校验并批量删除十六电月耗电量信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
