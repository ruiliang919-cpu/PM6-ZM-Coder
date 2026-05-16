package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseSimpleControlBo;
import com.ruoyi.zm.domain.vo.DevBaseSimpleControlVo;

import java.util.Collection;
import java.util.List;

/**
 * 普通时控启用Service接口
 *
 * @author ruoyi
 * @date 2024-09-09
 */
public interface IDevBaseSimpleControlService {

    /**
     * 查询普通时控启用
     */
    DevBaseSimpleControlVo queryById(Integer id);

    /**
     * 查询普通时控启用列表
     */
    TableDataInfo<DevBaseSimpleControlVo> queryPageList(DevBaseSimpleControlBo bo, PageQuery pageQuery);

    /**
     * 查询普通时控启用列表
     */
    List<DevBaseSimpleControlVo> queryList(DevBaseSimpleControlBo bo);

    /**
     * 新增普通时控启用
     */
    Boolean insertByBo(DevBaseSimpleControlBo bo);

    /**
     * 修改普通时控启用
     */
    Boolean updateByBo(DevBaseSimpleControlBo bo);

    /**
     * 校验并批量删除普通时控启用信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
