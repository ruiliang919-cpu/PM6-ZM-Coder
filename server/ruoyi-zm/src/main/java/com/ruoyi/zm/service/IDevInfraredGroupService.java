package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevInfraredGroupBo;
import com.ruoyi.zm.domain.vo.DevInfraredGroupVo;

import java.util.Collection;
import java.util.List;

/**
 * 红外模式传感器与分组选择Service接口
 *
 * @author ruoyi
 * @date 2024-09-04
 */
public interface IDevInfraredGroupService {

    /**
     * 查询红外模式传感器与分组选择
     */
    DevInfraredGroupVo queryById(Integer id);

    /**
     * 查询红外模式传感器与分组选择列表
     */
    TableDataInfo<DevInfraredGroupVo> queryPageList(DevInfraredGroupBo bo, PageQuery pageQuery);

    /**
     * 查询红外模式传感器与分组选择列表
     */
    List<DevInfraredGroupVo> queryList(DevInfraredGroupBo bo);

    /**
     * 新增红外模式传感器与分组选择
     */
    Boolean insertByBo(DevInfraredGroupBo bo);

    /**
     * 修改红外模式传感器与分组选择
     */
    Boolean updateByBo(DevInfraredGroupBo bo);

    /**
     * 校验并批量删除红外模式传感器与分组选择信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
