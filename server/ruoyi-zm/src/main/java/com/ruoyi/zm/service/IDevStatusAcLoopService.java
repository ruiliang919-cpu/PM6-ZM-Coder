package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevStatusAcLoopBo;
import com.ruoyi.zm.domain.vo.DevStatusAcLoopVo;

import java.util.Collection;
import java.util.List;

/**
 * 交流回路Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevStatusAcLoopService {

    /**
     * 查询交流回路
     */
    DevStatusAcLoopVo queryById(Long id);

    /**
     * 查询交流回路列表
     */
    TableDataInfo<DevStatusAcLoopVo> queryPageList(DevStatusAcLoopBo bo, PageQuery pageQuery);

    /**
     * 查询交流回路列表
     */
    List<DevStatusAcLoopVo> queryList(DevStatusAcLoopBo bo);

    /**
     * 新增交流回路
     */
    Boolean insertByBo(DevStatusAcLoopBo bo);

    /**
     * 修改交流回路
     */
    Boolean updateByBo(DevStatusAcLoopBo bo);

    /**
     * 校验并批量删除交流回路信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
