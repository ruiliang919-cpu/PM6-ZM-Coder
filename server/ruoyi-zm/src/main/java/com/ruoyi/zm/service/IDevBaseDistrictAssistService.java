package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevBaseDistrictAssistBo;
import com.ruoyi.zm.domain.vo.DevBaseDistrictAssistVo;

import java.util.Collection;
import java.util.List;

/**
 * 照明控制的分区控制的辅助Service接口
 *
 * @author ruoyi
 * @date 2024-08-31
 */
public interface IDevBaseDistrictAssistService {

    /**
     * 查询照明控制的分区控制的辅助
     */
    DevBaseDistrictAssistVo queryById(Integer id);

    /**
     * 查询照明控制的分区控制的辅助列表
     */
    TableDataInfo<DevBaseDistrictAssistVo> queryPageList(DevBaseDistrictAssistBo bo, PageQuery pageQuery);

    /**
     * 查询照明控制的分区控制的辅助列表
     */
    List<DevBaseDistrictAssistVo> queryList(DevBaseDistrictAssistBo bo);

    /**
     * 新增照明控制的分区控制的辅助
     */
    Boolean insertByBo(DevBaseDistrictAssistBo bo);

    /**
     * 修改照明控制的分区控制的辅助
     */
    Boolean updateByBo(DevBaseDistrictAssistBo bo);

    /**
     * 校验并批量删除照明控制的分区控制的辅助信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
