package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseRegionBo;
import com.ruoyi.zm.domain.vo.DevBaseRegionVo;

import java.util.Collection;
import java.util.List;

/**
 * 设备区域Service接口
 *
 * @author ruoyi
 * @date 2024-07-11
 */
public interface IDevBaseRegionService {

    /**
     * 查询设备区域
     */
    DevBaseRegionVo queryById(Long id);

    /**
     * 查询设备区域列表
     */
    TableDataInfo<DevBaseRegionVo> queryPageList(DevBaseRegionBo bo, PageQuery pageQuery);

    /**
     * 查询设备区域列表
     */
    List<DevBaseRegionVo> queryList(DevBaseRegionBo bo);

    /**
     * 新增设备区域
     */
    Boolean insertByBo(DevBaseRegionBo bo);

    /**
     * 修改设备区域
     */
    Boolean updateByBo(DevBaseRegionBo bo);

    /**
     * 校验并批量删除设备区域信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
