package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigInfraredSensorBo;
import com.ruoyi.zm.domain.vo.DevConfigInfraredSensorVo;

import java.util.Collection;
import java.util.List;

/**
 * 时控Service接口
 *
 * @author ruoyi
 * @date 2024-08-27
 */
public interface IDevConfigInfraredSensorService {

    /**
     * 查询时控
     */
    DevConfigInfraredSensorVo queryById(Integer id);

    /**
     * 查询时控列表
     */
    TableDataInfo<DevConfigInfraredSensorVo> queryPageList(DevConfigInfraredSensorBo bo, PageQuery pageQuery);

    /**
     * 查询时控列表
     */
    List<DevConfigInfraredSensorVo> queryList(DevConfigInfraredSensorBo bo);

    /**
     * 新增时控
     */
    Boolean insertByBo(DevConfigInfraredSensorBo bo);

    /**
     * 修改时控
     */
    Boolean updateByBo(DevConfigInfraredSensorBo bo);

    /**
     * 校验并批量删除时控信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
