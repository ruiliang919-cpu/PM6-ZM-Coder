package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigTimeBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeVo;

import java.util.Collection;
import java.util.List;

/**
 * 机柜时间设置Service接口
 *
 * @author ruoyi
 * @date 2024-08-26
 */
public interface IDevConfigTimeService {

    /**
     * 查询机柜时间设置
     */
    DevConfigTimeVo queryById(Integer id);

    /**
     * 查询机柜时间设置列表
     */
    TableDataInfo<DevConfigTimeVo> queryPageList(DevConfigTimeBo bo, PageQuery pageQuery);

    /**
     * 查询机柜时间设置列表
     */
    List<DevConfigTimeVo> queryList(DevConfigTimeBo bo);

    /**
     * 新增机柜时间设置
     */
    Boolean insertByBo(DevConfigTimeBo bo);

    /**
     * 修改机柜时间设置
     */
    Boolean updateByBo(DevConfigTimeBo bo);

    /**
     * 校验并批量删除机柜时间设置信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
