package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevFaultRecordBo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;

import java.util.Collection;
import java.util.List;

/**
 * 告警记录Service接口
 *
 * @author ruoyi
 * @date 2024-08-29
 */
public interface IDevFaultRecordService {

    /**
     * 查询告警记录
     */
    DevFaultRecordVo queryById(Integer id);

    /**
     * 查询告警记录列表
     */
    TableDataInfo<DevFaultRecordVo> queryPageList(DevFaultRecordBo bo, PageQuery pageQuery);

    /**
     * 查询告警记录列表
     */
    List<DevFaultRecordVo> queryList(DevFaultRecordBo bo);

    /**
     * 新增告警记录
     */
    Boolean insertByBo(DevFaultRecordBo bo);

    /**
     * 修改告警记录
     */
    Boolean updateByBo(DevFaultRecordBo bo);

    /**
     * 校验并批量删除告警记录信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);

    TableDataInfo<DevFaultRecordVo> queryPageList(DevFaultRecordBo bo, Integer pageSize, Integer pageNum);
}
