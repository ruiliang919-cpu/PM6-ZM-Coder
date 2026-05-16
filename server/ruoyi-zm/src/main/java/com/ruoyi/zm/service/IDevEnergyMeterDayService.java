package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevEnergyMeterDayBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterDayVo;

import java.util.Collection;
import java.util.List;

/**
 * 十六电日耗电量Service接口
 *
 * @author ruoyi
 * @date 2024-09-12
 */
public interface IDevEnergyMeterDayService {

    /**
     * 查询十六电日耗电量
     */
    DevEnergyMeterDayVo queryById(Integer id);

    /**
     * 查询十六电日耗电量列表
     */
    TableDataInfo<DevEnergyMeterDayVo> queryPageList(DevEnergyMeterDayBo bo, PageQuery pageQuery);

    /**
     * 查询十六电日耗电量列表
     */
    List<DevEnergyMeterDayVo> queryList(DevEnergyMeterDayBo bo);

    /**
     * 新增十六电日耗电量
     */
    Boolean insertByBo(DevEnergyMeterDayBo bo);

    /**
     * 修改十六电日耗电量
     */
    Boolean updateByBo(DevEnergyMeterDayBo bo);

    /**
     * 校验并批量删除十六电日耗电量信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
