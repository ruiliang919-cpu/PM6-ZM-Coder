package com.ruoyi.zm.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.bo.DevInstructBo;
import com.ruoyi.zm.domain.vo.DevInstructVo;

import java.util.Collection;
import java.util.List;

/**
 * 读取协议的指令下发Service接口
 *
 * @author ruoyi
 * @date 2024-09-05
 */
public interface IDevInstructService {

    /**
     * 查询读取协议的指令下发
     */
    DevInstructVo queryById(Long id);

    /**
     * 查询读取协议的指令下发列表
     */
    TableDataInfo<DevInstructVo> queryPageList(DevInstructBo bo, PageQuery pageQuery);

    /**
     * 查询读取协议的指令下发列表
     */
    List<DevInstructVo> queryList(DevInstructBo bo);

    /**
     * 新增读取协议的指令下发
     */
    Boolean insertByBo(DevInstructBo bo);

    /**
     * 修改读取协议的指令下发
     */
    Boolean updateByBo(DevInstructBo bo);

    /**
     * 校验并批量删除读取协议的指令下发信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
