package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.bo.DevBaseDistrictBo;
import com.ruoyi.zm.domain.vo.DevBaseDistrictVo;

import java.util.List;

/**
 * 控制分区Service接口
 *
 * @author mophi
 * @date 2024-08-31
 */
public interface IDevBaseDistrictService {

    /**
     * 查询控制分区
     */
    DevBaseDistrict queryById(Long id);

    /**
     * 查询控制分区列表
     */
    TableDataInfo<DevBaseDistrictVo> queryPageList(DevBaseDistrictBo bo, PageQuery pageQuery);

    /**
     * 查询控制分区列表
     */
    List<DevBaseDistrictVo> queryList(DevBaseDistrictBo bo);

    /**
     * 新增控制分区
     */
    Boolean insertByBo(DevBaseDistrictBo bo);

    /**
     * 修改控制分区
     */
    Boolean updateByBo(DevBaseDistrictBo bo);

    /**
     * 校验并批量删除控制分区信息
     */
    Boolean deleteWithValidByIds(Long[] ids);
}
