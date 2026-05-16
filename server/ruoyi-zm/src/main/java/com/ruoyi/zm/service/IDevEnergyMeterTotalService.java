package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevEnergyMeterTotalBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterTotalVo;

import java.util.Collection;
import java.util.List;

/**
 * 十六电总耗电量Service接口
 *
 * @author ruoyi
 * @date 2024-09-12
 */
public interface IDevEnergyMeterTotalService {

    /**
     * 查询十六电总耗电量
     */
    DevEnergyMeterTotalVo queryById(Integer id);

    /**
     * 查询十六电总耗电量列表
     */
    TableDataInfo<DevEnergyMeterTotalVo> queryPageList(DevEnergyMeterTotalBo bo, PageQuery pageQuery);

    /**
     * 查询十六电总耗电量列表
     */
    List<DevEnergyMeterTotalVo> queryList(DevEnergyMeterTotalBo bo);

    /**
     * 新增十六电总耗电量
     */
    Boolean insertByBo(DevEnergyMeterTotalBo bo);

    /**
     * 修改十六电总耗电量
     */
    Boolean updateByBo(DevEnergyMeterTotalBo bo);

    /**
     * 校验并批量删除十六电总耗电量信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
