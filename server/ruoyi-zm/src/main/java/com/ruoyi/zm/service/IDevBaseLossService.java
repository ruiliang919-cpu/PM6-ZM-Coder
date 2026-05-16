package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseLossBo;
import com.ruoyi.zm.domain.vo.DevBaseLossVo;

import java.util.Collection;
import java.util.List;

/**
 * 总功率Service接口
 *
 * @author ruoyi
 * @date 2024-08-29
 */
public interface IDevBaseLossService {

    /**
     * 查询总功率
     */
    DevBaseLossVo queryById(Integer id);

    /**
     * 查询总功率列表
     */
    TableDataInfo<DevBaseLossVo> queryPageList(DevBaseLossBo bo, PageQuery pageQuery);

    /**
     * 查询总功率列表
     */
    List<DevBaseLossVo> queryList(DevBaseLossBo bo);

    /**
     * 新增总功率
     */
    Boolean insertByBo(DevBaseLossBo bo);

    /**
     * 修改总功率
     */
    Boolean updateByBo(DevBaseLossBo bo);

    /**
     * 校验并批量删除总功率信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
