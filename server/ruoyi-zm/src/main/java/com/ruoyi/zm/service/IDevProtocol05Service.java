package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevProtocol05;
import com.ruoyi.zm.domain.bo.DevProtocol05Bo;
import com.ruoyi.zm.domain.vo.DevProtocol05Vo;

import java.util.Collection;
import java.util.List;

/**
 * 遥调协议Service接口
 *
 * @author ruoyi
 * @date 2024-07-19
 */
public interface IDevProtocol05Service {

    /**
     * 获取05列表的所有地址数据，并存入到Redis中
     */
    List<DevProtocol05> get05List();

    /**
     * 查询遥调协议
     */
    DevProtocol05Vo queryById(Long id);

    /**
     * 查询遥调协议列表
     */
    TableDataInfo<DevProtocol05Vo> queryPageList(DevProtocol05Bo bo, PageQuery pageQuery);

    /**
     * 查询遥调协议列表
     */
    List<DevProtocol05Vo> queryList(DevProtocol05Bo bo);

    /**
     * 新增遥调协议
     */
    Boolean insertByBo(DevProtocol05Bo bo);

    /**
     * 修改遥调协议
     */
    Boolean updateByBo(DevProtocol05Bo bo);

    /**
     * 校验并批量删除遥调协议信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据遥调协议的地址找到对应的名称
     * @param addr
     * @return
     */
    DevProtocol05 getByProtocolAddr(String addr);
}
