package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevEnergyMeterWeekBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterWeekVo;

import java.util.Collection;
import java.util.List;

/**
 * 十六电周耗电量Service接口
 *
 * @author ruoyi
 * @date 2024-09-12
 */
public interface IDevEnergyMeterWeekService {

    /**
     * 查询十六电周耗电量
     */
    DevEnergyMeterWeekVo queryById(Integer id);

    /**
     * 查询十六电周耗电量列表
     */
    TableDataInfo<DevEnergyMeterWeekVo> queryPageList(DevEnergyMeterWeekBo bo, PageQuery pageQuery);

    /**
     * 查询十六电周耗电量列表
     */
    List<DevEnergyMeterWeekVo> queryList(DevEnergyMeterWeekBo bo);

    /**
     * 新增十六电周耗电量
     */
    Boolean insertByBo(DevEnergyMeterWeekBo bo);

    /**
     * 修改十六电周耗电量
     */
    Boolean updateByBo(DevEnergyMeterWeekBo bo);

    /**
     * 校验并批量删除十六电周耗电量信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
