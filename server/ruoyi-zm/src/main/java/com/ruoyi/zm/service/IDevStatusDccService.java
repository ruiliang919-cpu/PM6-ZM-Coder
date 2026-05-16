package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevStatusDccBo;
import com.ruoyi.zm.domain.vo.DevStatusDccVo;

import java.util.Collection;
import java.util.List;

/**
 * 直流信息Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevStatusDccService {

    /**
     * 查询直流信息
     */
    DevStatusDccVo queryById(Long id);

    /**
     * 查询直流信息列表
     */
    TableDataInfo<DevStatusDccVo> queryPageList(DevStatusDccBo bo, PageQuery pageQuery);

    /**
     * 查询直流信息列表
     */
    List<DevStatusDccVo> queryList(DevStatusDccBo bo);

    /**
     * 新增直流信息
     */
    Boolean insertByBo(DevStatusDccBo bo);

    /**
     * 修改直流信息
     */
    Boolean updateByBo(DevStatusDccBo bo);

    /**
     * 校验并批量删除直流信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
