package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevStatusBranchBo;
import com.ruoyi.zm.domain.vo.DevStatusBranchVo;

import java.util.Collection;
import java.util.List;

/**
 * 支路信息记录Service接口
 *
 * @author ruoyi
 * @date 2024-08-29
 */
public interface IDevStatusBranchService {

    /**
     * 查询支路信息记录
     */
    DevStatusBranchVo queryById(Long id);

    /**
     * 查询支路信息记录列表
     */
    TableDataInfo<DevStatusBranchVo> queryPageList(DevStatusBranchBo bo, PageQuery pageQuery);

    /**
     * 查询支路信息记录列表
     */
    List<DevStatusBranchVo> queryList(DevStatusBranchBo bo);

    /**
     * 新增支路信息记录
     */
    Boolean insertByBo(DevStatusBranchBo bo);

    /**
     * 修改支路信息记录
     */
    Boolean updateByBo(DevStatusBranchBo bo);

    /**
     * 校验并批量删除支路信息记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
