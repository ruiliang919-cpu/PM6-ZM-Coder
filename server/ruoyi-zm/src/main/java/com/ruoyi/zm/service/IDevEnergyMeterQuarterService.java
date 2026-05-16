package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevEnergyMeterQuarterBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterQuarterVo;

import java.util.Collection;
import java.util.List;

/**
 * 十六电季耗电量Service接口
 *
 * @author ruoyi
 * @date 2024-09-12
 */
public interface IDevEnergyMeterQuarterService {

    /**
     * 查询十六电季耗电量
     */
    DevEnergyMeterQuarterVo queryById(Integer id);

    /**
     * 查询十六电季耗电量列表
     */
    TableDataInfo<DevEnergyMeterQuarterVo> queryPageList(DevEnergyMeterQuarterBo bo, PageQuery pageQuery);

    /**
     * 查询十六电季耗电量列表
     */
    List<DevEnergyMeterQuarterVo> queryList(DevEnergyMeterQuarterBo bo);

    /**
     * 新增十六电季耗电量
     */
    Boolean insertByBo(DevEnergyMeterQuarterBo bo);

    /**
     * 修改十六电季耗电量
     */
    Boolean updateByBo(DevEnergyMeterQuarterBo bo);

    /**
     * 校验并批量删除十六电季耗电量信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
