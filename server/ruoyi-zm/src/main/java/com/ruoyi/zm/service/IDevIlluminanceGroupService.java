package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevIlluminanceGroupBo;
import com.ruoyi.zm.domain.vo.DevIlluminanceGroupVo;

import java.util.Collection;
import java.util.List;

/**
 * 照度模式传感器与分组选择Service接口
 *
 * @author ruoyi
 * @date 2024-09-04
 */
public interface IDevIlluminanceGroupService {

    /**
     * 查询照度模式传感器与分组选择
     */
    DevIlluminanceGroupVo queryById(Integer id);

    /**
     * 查询照度模式传感器与分组选择列表
     */
    TableDataInfo<DevIlluminanceGroupVo> queryPageList(DevIlluminanceGroupBo bo, PageQuery pageQuery);

    /**
     * 查询照度模式传感器与分组选择列表
     */
    List<DevIlluminanceGroupVo> queryList(DevIlluminanceGroupBo bo);

    /**
     * 新增照度模式传感器与分组选择
     */
    Boolean insertByBo(DevIlluminanceGroupBo bo);

    /**
     * 修改照度模式传感器与分组选择
     */
    Boolean updateByBo(DevIlluminanceGroupBo bo);

    /**
     * 校验并批量删除照度模式传感器与分组选择信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
