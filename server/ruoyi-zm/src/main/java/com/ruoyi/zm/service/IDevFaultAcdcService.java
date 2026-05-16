package com.ruoyi.zm.service;


import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevFaultAcdcBo;
import com.ruoyi.zm.domain.vo.DevFaultAcdcVo;

import java.util.Collection;
import java.util.List;

/**
 * AC/DC故障信息Service接口
 *
 * @author ruoyi
 * @date 2024-08-24
 */
public interface IDevFaultAcdcService {

    /**
     * 查询AC/DC故障信息
     */
    DevFaultAcdcVo queryById(Long id);

    /**
     * 查询AC/DC故障信息列表
     */
    TableDataInfo<DevFaultAcdcVo> queryPageList(DevFaultAcdcBo bo, PageQuery pageQuery);

    /**
     * 查询AC/DC故障信息列表
     */
    List<DevFaultAcdcVo> queryList(DevFaultAcdcBo bo);

    /**
     * 新增AC/DC故障信息
     */
    Boolean insertByBo(DevFaultAcdcBo bo);

    /**
     * 修改AC/DC故障信息
     */
    Boolean updateByBo(DevFaultAcdcBo bo);

    /**
     * 校验并批量删除AC/DC故障信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
