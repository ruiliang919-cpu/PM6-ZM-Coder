package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigGroupSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigGroupSensorVo;

import java.util.Collection;
import java.util.List;

/**
 * 传感器与分组对应Service接口
 *
 * @author ruoyi
 * @date 2024-08-27
 */
public interface IDevConfigGroupSensorService {

    /**
     * 查询传感器与分组对应
     */
    DevConfigGroupSensorVo queryById(Integer id);

    /**
     * 查询传感器与分组对应列表
     */
    TableDataInfo<DevConfigGroupSensorVo> queryPageList(DevConfigGroupSensorBo bo, PageQuery pageQuery);

    /**
     * 查询传感器与分组对应列表
     */
    List<DevConfigGroupSensorVo> queryList(DevConfigGroupSensorBo bo);

    /**
     * 新增传感器与分组对应
     */
    Boolean insertByBo(DevConfigGroupSensorBo bo);

    /**
     * 修改传感器与分组对应
     */
    Boolean updateByBo(DevConfigGroupSensorBo bo);

    /**
     * 校验并批量删除传感器与分组对应信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
