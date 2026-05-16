package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlVo;

import java.util.Collection;
import java.util.List;

/**
 * 普通模式-时控配置Service接口
 *
 * @author ruoyi
 * @date 2024-08-26
 */
public interface IDevConfigTimeControlService {

    /**
     * 查询普通模式-时控配置
     */
    DevConfigTimeControlVo queryById(Long id);

    /**
     * 查询普通模式-时控配置列表
     */
    TableDataInfo<DevConfigTimeControlVo> queryPageList(DevConfigTimeControlBo bo, PageQuery pageQuery);

    /**
     * 查询普通模式-时控配置列表
     */
    List<DevConfigTimeControlVo> queryList(DevConfigTimeControlBo bo);

    /**
     * 新增普通模式-时控配置
     */
    Boolean insertByBo(DevConfigTimeControlBo bo);

    /**
     * 修改普通模式-时控配置
     */
    Boolean updateByBo(DevConfigTimeControlBo bo);

    /**
     * 校验并批量删除普通模式-时控配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
