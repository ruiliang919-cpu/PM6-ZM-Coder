package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevEnergyMeterPowerBo;
import com.ruoyi.zm.domain.vo.DevEnergyMeterPowerVo;

import java.util.Collection;
import java.util.List;

/**
 * 十六电总功率Service接口
 *
 * @author ruoyi
 * @date 2024-09-12
 */
public interface IDevEnergyMeterPowerService {

    /**
     * 查询十六电总功率
     */
    DevEnergyMeterPowerVo queryById(Integer id);

    /**
     * 查询十六电总功率列表
     */
    TableDataInfo<DevEnergyMeterPowerVo> queryPageList(DevEnergyMeterPowerBo bo, PageQuery pageQuery);

    /**
     * 查询十六电总功率列表
     */
    List<DevEnergyMeterPowerVo> queryList(DevEnergyMeterPowerBo bo);

    /**
     * 新增十六电总功率
     */
    Boolean insertByBo(DevEnergyMeterPowerBo bo);

    /**
     * 修改十六电总功率
     */
    Boolean updateByBo(DevEnergyMeterPowerBo bo);

    /**
     * 校验并批量删除十六电总功率信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
