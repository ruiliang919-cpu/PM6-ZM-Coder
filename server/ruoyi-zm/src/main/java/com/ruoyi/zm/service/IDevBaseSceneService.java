package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseSceneBo;
import com.ruoyi.zm.domain.vo.DevBaseSceneVo;

import java.util.Collection;
import java.util.List;

/**
 * 场景Service接口
 *
 * @author mophi
 * @date 2024-07-11
 */
public interface IDevBaseSceneService {

    /**
     * 查询场景
     */
    DevBaseSceneVo queryById(Long id);

    /**
     * 查询场景列表
     */
    TableDataInfo<DevBaseSceneVo> queryPageList(DevBaseSceneBo bo, PageQuery pageQuery);

    /**
     * 查询场景列表
     */
    List<DevBaseSceneVo> queryList(DevBaseSceneBo bo);

    /**
     * 新增场景
     */
    Boolean insertByBo(DevBaseSceneBo bo);

    /**
     * 修改场景
     */
    Boolean updateByBo(DevBaseSceneBo bo);

    /**
     * 校验并批量删除场景信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
