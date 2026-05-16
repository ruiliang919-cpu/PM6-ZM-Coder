package com.ruoyi.zm.service;


import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigGroupBo;
import com.ruoyi.zm.domain.vo.DevConfigGroupVo;

import java.util.Collection;
import java.util.List;

/**
 * 回路分组Service接口
 *
 * @author ruoyi
 * @date 2024-08-16
 */
public interface IDevConfigGroupService {

    /**
     * 查询分组回路
     */
    DevConfigGroupVo queryById(Long id);

    /**
     * 查询分组回路列表
     */
    TableDataInfo<DevConfigGroupVo> queryPageList(DevConfigGroupBo bo, PageQuery pageQuery);

    /**
     * 查询分组回路列表
     */
    List<DevConfigGroupVo> queryList(DevConfigGroupBo bo);

    /**
     * 新增分组回路
     */
    Boolean insertByBo(DevConfigGroupBo bo);

    /**
     * 修改分组回路
     */
    Boolean updateByBo(DevConfigGroupBo bo);

    /**
     * 校验并批量删除分组回路信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


}
