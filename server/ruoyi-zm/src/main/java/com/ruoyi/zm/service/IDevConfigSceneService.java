package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigSceneBo;
import com.ruoyi.zm.domain.vo.DevConfigSceneVo;

import java.util.Collection;
import java.util.List;

/**
 * 场景设置Service接口
 *
 * @author ruoyi
 * @date 2024-08-26
 */
public interface IDevConfigSceneService {

    /**
     * 查询场景设置
     */
    DevConfigSceneVo queryById(Long id);

    /**
     * 查询场景设置列表
     */
    TableDataInfo<DevConfigSceneVo> queryPageList(DevConfigSceneBo bo, PageQuery pageQuery);

    /**
     * 查询场景设置列表
     */
    List<DevConfigSceneVo> queryList(DevConfigSceneBo bo);

    /**
     * 新增场景设置
     */
    Boolean insertByBo(DevConfigSceneBo bo);

    /**
     * 修改场景设置
     */
    Boolean updateByBo(DevConfigSceneBo bo);

    /**
     * 校验并批量删除场景设置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
