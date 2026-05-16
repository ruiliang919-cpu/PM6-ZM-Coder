package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.StatusAcDc;
import com.ruoyi.zm.domain.bo.StatusAcDcBo;
import com.ruoyi.zm.domain.vo.StatusAcDcVo;

import java.util.List;

/**
 * 状态：交窜直 ,交流电（AC）与直流电（DC）Service接口
 */
public interface StatusAcDcService {
    /**
     * 查询AC/DC回路列表 分页
     */
    TableDataInfo<StatusAcDcVo> queryPageList(StatusAcDcBo bo, PageQuery pageQuery);

    /**
     * 查询AC/DC回路列表
     */
    List<StatusAcDc> queryList(int salveId);
}
