package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigDistrictBo;
import com.ruoyi.zm.domain.vo.DevConfigDistrictVo;

import java.util.Collection;
import java.util.List;

/**
 * 分区配置Service接口
 *
 * @author ruoyi
 * @date 2024-08-26
 */
public interface IDevConfigDistrictService {

    /**
     * 查询分区配置
     */
    DevConfigDistrictVo queryById(Long id);

    /**
     * 查询分区配置列表
     */
    TableDataInfo<DevConfigDistrictVo> queryPageList(DevConfigDistrictBo bo, PageQuery pageQuery);

    /**
     * 查询分区配置列表
     */
    List<DevConfigDistrictVo> queryList(DevConfigDistrictBo bo);

    /**
     * 新增分区配置
     */
    Boolean insertByBo(DevConfigDistrictBo bo);

    /**
     * 修改分区配置
     */
    Boolean updateByBo(DevConfigDistrictBo bo);

    /**
     * 校验并批量删除分区配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
