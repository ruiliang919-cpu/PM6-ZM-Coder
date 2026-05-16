package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseSceneControlBo;
import com.ruoyi.zm.domain.vo.DevBaseSceneControlVo;

import java.util.Collection;
import java.util.List;

/**
 * 场景时控启用Service接口
 *
 * @author ruoyi
 * @date 2024-09-09
 */
public interface IDevBaseSceneControlService {

    /**
     * 查询场景时控启用
     */
    DevBaseSceneControlVo queryById(Integer id);

    /**
     * 查询场景时控启用列表
     */
    TableDataInfo<DevBaseSceneControlVo> queryPageList(DevBaseSceneControlBo bo, PageQuery pageQuery);

    /**
     * 查询场景时控启用列表
     */
    List<DevBaseSceneControlVo> queryList(DevBaseSceneControlBo bo);

    /**
     * 新增场景时控启用
     */
    Boolean insertByBo(DevBaseSceneControlBo bo);

    /**
     * 修改场景时控启用
     */
    Boolean updateByBo(DevBaseSceneControlBo bo);

    /**
     * 校验并批量删除场景时控启用信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
