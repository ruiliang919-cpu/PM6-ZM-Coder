package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigIlluminanceSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigIlluminanceSensorVo;

import java.util.Collection;
import java.util.List;

/**
 * 系统设置-控制方式-照度模式 传感器Service接口
 *
 * @author ruoyi
 * @date 2024-08-29
 */
public interface IDevConfigIlluminanceSensorService {

    /**
     * 查询系统设置-控制方式-照度模式 传感器
     */
    DevConfigIlluminanceSensorVo queryById(Integer id);

    /**
     * 查询系统设置-控制方式-照度模式 传感器列表
     */
    TableDataInfo<DevConfigIlluminanceSensorVo> queryPageList(DevConfigIlluminanceSensorBo bo, PageQuery pageQuery);

    /**
     * 查询系统设置-控制方式-照度模式 传感器列表
     */
    List<DevConfigIlluminanceSensorVo> queryList(DevConfigIlluminanceSensorBo bo);

    /**
     * 新增系统设置-控制方式-照度模式 传感器
     */
    Boolean insertByBo(DevConfigIlluminanceSensorBo bo);

    /**
     * 修改系统设置-控制方式-照度模式 传感器
     */
    Boolean updateByBo(DevConfigIlluminanceSensorBo bo);

    /**
     * 校验并批量删除系统设置-控制方式-照度模式 传感器信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
