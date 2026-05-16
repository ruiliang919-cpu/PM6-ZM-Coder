package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevStatusDccLoopBo;
import com.ruoyi.zm.domain.vo.DevStatusDccLoopVo;
import com.serotonin.modbus4j.exception.ModbusTransportException;

import java.util.Collection;
import java.util.List;

/**
 * 直流回路Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevStatusDccLoopService {

    /**
     * 查询直流回路
     */
    DevStatusDccLoopVo queryById(Long id);

    /**
     * 查询直流回路列表
     */
    TableDataInfo<DevStatusDccLoopVo> queryPageList(DevStatusDccLoopBo bo, PageQuery pageQuery);

    /**
     * 查询直流回路列表
     */
    List<DevStatusDccLoopVo> queryList(DevStatusDccLoopBo bo);

    /**
     * 新增直流回路
     */
    Boolean insertByBo(DevStatusDccLoopBo bo);

    /**
     * 修改直流回路
     */
    Boolean updateByBo(DevStatusDccLoopBo bo);

    /**
     * 校验并批量删除直流回路信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据设备ID与分组编号更新对应回路的分组信息
     */
    Boolean updateLoopByGroup(int slaveId, int groupId) throws ModbusTransportException;
}
