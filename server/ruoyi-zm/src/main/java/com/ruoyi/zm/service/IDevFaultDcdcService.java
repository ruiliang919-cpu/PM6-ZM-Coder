package com.ruoyi.zm.service;


import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevFaultDcdcBo;
import com.ruoyi.zm.domain.vo.DevFaultDcdcVo;

import java.util.Collection;
import java.util.List;

/**
 * DC/DC故障信息Service接口
 *
 * @author ruoyi
 * @date 2024-08-24
 */
public interface IDevFaultDcdcService {

    /**
     * 查询DC/DC故障信息
     */
    DevFaultDcdcVo queryById(Long id);

    /**
     * 查询DC/DC故障信息列表
     */
    TableDataInfo<DevFaultDcdcVo> queryPageList(DevFaultDcdcBo bo, PageQuery pageQuery);

    /**
     * 查询DC/DC故障信息列表
     */
    List<DevFaultDcdcVo> queryList(DevFaultDcdcBo bo);

    /**
     * 新增DC/DC故障信息
     */
    Boolean insertByBo(DevFaultDcdcBo bo);

    /**
     * 修改DC/DC故障信息
     */
    Boolean updateByBo(DevFaultDcdcBo bo);

    /**
     * 校验并批量删除DC/DC故障信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
