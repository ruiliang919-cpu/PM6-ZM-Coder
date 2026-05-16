package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevProtocol06;
import com.ruoyi.zm.domain.bo.DevProtocol06Bo;
import com.ruoyi.zm.domain.vo.DevProtocol06Vo;

import java.util.Collection;
import java.util.List;

/**
 * 遥控协议Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevProtocol06Service {

    /**
     * 查询遥控协议
     */
    DevProtocol06Vo queryById(Long id);

    /**
     * 查询遥控协议列表
     */
    TableDataInfo<DevProtocol06Vo> queryPageList(DevProtocol06Bo bo, PageQuery pageQuery);

    /**
     * 查询遥控协议列表
     */
    List<DevProtocol06Vo> queryList(DevProtocol06Bo bo);

    /**
     * 新增遥控协议
     */
    Boolean insertByBo(DevProtocol06Bo bo);

    /**
     * 修改遥控协议
     */
    Boolean updateByBo(DevProtocol06Bo bo);

    /**
     * 校验并批量删除遥控协议信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<DevProtocol06> get06List();
}
