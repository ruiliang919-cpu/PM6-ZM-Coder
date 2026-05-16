package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigSimpleGroupBo;
import com.ruoyi.zm.domain.vo.DevConfigSimpleGroupVo;

import java.util.Collection;
import java.util.List;

/**
 * 普通时控模式与分组配置Service接口
 *
 * @author ruoyi
 * @date 2024-09-03
 */
public interface IDevConfigSimpleGroupService {

    /**
     * 查询普通时控模式与分组配置
     */
    DevConfigSimpleGroupVo queryById(Integer id);

    /**
     * 查询普通时控模式与分组配置列表
     */
    TableDataInfo<DevConfigSimpleGroupVo> queryPageList(DevConfigSimpleGroupBo bo, PageQuery pageQuery);

    /**
     * 查询普通时控模式与分组配置列表
     */
    List<DevConfigSimpleGroupVo> queryList(DevConfigSimpleGroupBo bo);

    /**
     * 新增普通时控模式与分组配置
     */
    Boolean insertByBo(DevConfigSimpleGroupBo bo);

    /**
     * 修改普通时控模式与分组配置
     */
    Boolean updateByBo(DevConfigSimpleGroupBo bo);

    /**
     * 校验并批量删除普通时控模式与分组配置信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
