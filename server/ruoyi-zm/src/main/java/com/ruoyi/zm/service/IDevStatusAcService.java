package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevStatusAcBo;
import com.ruoyi.zm.domain.vo.DevStatusAcVo;

import java.util.Collection;
import java.util.List;

/**
 * 交流信息Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevStatusAcService {

    /**
     * 查询交流信息
     */
    DevStatusAcVo queryById(Long id);

    /**
     * 查询交流信息列表
     */
    TableDataInfo<DevStatusAcVo> queryPageList(DevStatusAcBo bo, PageQuery pageQuery);

    /**
     * 查询交流信息列表
     */
    List<DevStatusAcVo> queryList(DevStatusAcBo bo);

    /**
     * 新增交流信息
     */
    Boolean insertByBo(DevStatusAcBo bo);

    /**
     * 修改交流信息
     */
    Boolean updateByBo(DevStatusAcBo bo);

    /**
     * 校验并批量删除交流信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
