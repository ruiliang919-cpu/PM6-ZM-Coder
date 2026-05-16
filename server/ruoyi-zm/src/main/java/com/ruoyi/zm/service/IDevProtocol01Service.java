package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevProtocol01Bo;
import com.ruoyi.zm.domain.vo.DevProtocol01Vo;

import java.util.Collection;
import java.util.List;

/**
 * 遥信协议Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevProtocol01Service {

    /**
     * 查询遥信协议
     */
    DevProtocol01Vo queryById(Long id);

    /**
     * 查询遥信协议列表
     */
    TableDataInfo<DevProtocol01Vo> queryPageList(DevProtocol01Bo bo, PageQuery pageQuery);

    /**
     * 查询遥信协议列表
     */
    List<DevProtocol01Vo> queryList(DevProtocol01Bo bo);

    /**
     * 新增遥信协议
     */
    Boolean insertByBo(DevProtocol01Bo bo);

    /**
     * 修改遥信协议
     */
    Boolean updateByBo(DevProtocol01Bo bo);

    /**
     * 校验并批量删除遥信协议信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
