package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevProtocol03Bo;
import com.ruoyi.zm.domain.vo.DevProtocol03Vo;

import java.util.Collection;
import java.util.List;

/**
 * 遥测协议Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevProtocol03Service {

    /**
     * 查询遥测协议
     */
    DevProtocol03Vo queryById(Long id);

    /**
     * 查询遥测协议列表
     */
    TableDataInfo<DevProtocol03Vo> queryPageList(DevProtocol03Bo bo, PageQuery pageQuery);

    /**
     * 查询遥测协议列表
     */
    List<DevProtocol03Vo> queryList(DevProtocol03Bo bo);

    /**
     * 新增遥测协议
     */
    Boolean insertByBo(DevProtocol03Bo bo);

    /**
     * 修改遥测协议
     */
    Boolean updateByBo(DevProtocol03Bo bo);

    /**
     * 校验并批量删除遥测协议信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
