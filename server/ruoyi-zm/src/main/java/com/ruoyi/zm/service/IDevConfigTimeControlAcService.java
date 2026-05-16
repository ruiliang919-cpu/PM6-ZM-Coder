package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlAcBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlAcVo;

import java.util.Collection;
import java.util.List;

/**
 * 系统设置-控制方式-交流开关-时段信息 Service接口
 *
 * @author ruoyi
 * @date 2024-08-27
 */
public interface IDevConfigTimeControlAcService {

    /**
     * 查询系统设置-控制方式-交流开关-时段信息
     */
    DevConfigTimeControlAcVo queryById(Integer id);

    /**
     * 查询系统设置-控制方式-交流开关-时段信息 列表
     */
    TableDataInfo<DevConfigTimeControlAcVo> queryPageList(DevConfigTimeControlAcBo bo, PageQuery pageQuery);

    /**
     * 查询系统设置-控制方式-交流开关-时段信息 列表
     */
    List<DevConfigTimeControlAcVo> queryList(DevConfigTimeControlAcBo bo);

    /**
     * 新增系统设置-控制方式-交流开关-时段信息
     */
    Boolean insertByBo(DevConfigTimeControlAcBo bo);

    /**
     * 修改系统设置-控制方式-交流开关-时段信息
     */
    Boolean updateByBo(DevConfigTimeControlAcBo bo);

    /**
     * 校验并批量删除系统设置-控制方式-交流开关-时段信息 信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
