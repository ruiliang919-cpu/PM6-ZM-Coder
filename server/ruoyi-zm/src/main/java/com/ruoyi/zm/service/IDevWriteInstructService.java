package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevWriteInstructBo;
import com.ruoyi.zm.domain.vo.DevWriteInstructVo;

import java.util.Collection;
import java.util.List;

/**
 * 写入协议的指令下发Service接口
 *
 * @author ruoyi
 * @date 2024-09-05
 */
public interface IDevWriteInstructService {

    /**
     * 查询写入协议的指令下发
     */
    DevWriteInstructVo queryById(Long id);

    /**
     * 查询写入协议的指令下发列表
     */
    TableDataInfo<DevWriteInstructVo> queryPageList(DevWriteInstructBo bo, PageQuery pageQuery);

    /**
     * 查询写入协议的指令下发列表
     */
    List<DevWriteInstructVo> queryList(DevWriteInstructBo bo);

    /**
     * 新增写入协议的指令下发
     */
    Boolean insertByBo(DevWriteInstructBo bo);

    /**
     * 修改写入协议的指令下发
     */
    Boolean updateByBo(DevWriteInstructBo bo);

    /**
     * 校验并批量删除写入协议的指令下发信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
