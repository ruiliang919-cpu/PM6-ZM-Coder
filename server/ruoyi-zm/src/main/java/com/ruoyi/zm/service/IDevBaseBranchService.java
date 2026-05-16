package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseBranchBo;
import com.ruoyi.zm.domain.vo.DevBaseBranchVo;

import java.util.Collection;
import java.util.List;

/**
 * 馈线支路名称Service接口
 *
 * @author ruoyi
 * @date 2024-08-29
 */
public interface IDevBaseBranchService {

    /**
     * 查询馈线支路名称
     */
    DevBaseBranchVo queryById(Long id);

    /**
     * 查询馈线支路名称列表
     */
    TableDataInfo<DevBaseBranchVo> queryPageList(DevBaseBranchBo bo, PageQuery pageQuery);

    /**
     * 查询馈线支路名称列表
     */
    List<DevBaseBranchVo> queryList(DevBaseBranchBo bo);

    /**
     * 新增馈线支路名称
     */
    Boolean insertByBo(DevBaseBranchBo bo);

    /**
     * 修改馈线支路名称
     */
    Boolean updateByBo(DevBaseBranchBo bo);

    /**
     * 校验并批量删除馈线支路名称信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
