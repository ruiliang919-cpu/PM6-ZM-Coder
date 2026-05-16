package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevConfigTimeControlSceneBo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlSceneVo;

import java.util.Collection;
import java.util.List;

/**
 * 场景模式-时控配置Service接口
 *
 * @author ruoyi
 * @date 2024-08-27
 */
public interface IDevConfigTimeControlSceneService {

    /**
     * 查询场景模式-时控配置
     */
    DevConfigTimeControlSceneVo queryById(Integer id);

    /**
     * 查询场景模式-时控配置列表
     */
    TableDataInfo<DevConfigTimeControlSceneVo> queryPageList(DevConfigTimeControlSceneBo bo, PageQuery pageQuery);

    /**
     * 查询场景模式-时控配置列表
     */
    List<DevConfigTimeControlSceneVo> queryList(DevConfigTimeControlSceneBo bo);

    /**
     * 新增场景模式-时控配置
     */
    Boolean insertByBo(DevConfigTimeControlSceneBo bo);

    /**
     * 修改场景模式-时控配置
     */
    Boolean updateByBo(DevConfigTimeControlSceneBo bo);

    /**
     * 校验并批量删除场景模式-时控配置信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
