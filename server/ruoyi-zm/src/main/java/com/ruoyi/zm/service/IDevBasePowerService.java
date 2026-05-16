package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBasePowerBo;
import com.ruoyi.zm.domain.vo.DevBasePowerVo;

import java.util.Collection;
import java.util.List;

/**
 * 机柜耗电量 功率 Service接口
 *
 * @author ruoyi
 * @date 2024-08-29
 */
public interface IDevBasePowerService {

    /**
     * 查询机柜耗电量 功率
     */
    DevBasePowerVo queryById(Integer id);

    /**
     * 查询机柜耗电量 功率 列表
     */
    TableDataInfo<DevBasePowerVo> queryPageList(DevBasePowerBo bo, PageQuery pageQuery);

    /**
     * 查询机柜耗电量 功率 列表
     */
    List<DevBasePowerVo> queryList(DevBasePowerBo bo);

    /**
     * 新增机柜耗电量 功率
     */
    Boolean insertByBo(DevBasePowerBo bo);

    /**
     * 修改机柜耗电量 功率
     */
    Boolean updateByBo(DevBasePowerBo bo);

    /**
     * 校验并批量删除机柜耗电量 功率 信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
