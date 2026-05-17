package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * 协议 Service 泛型基类
 * 提取 Protocol01/03/05/06 的通用 CRUD 逻辑
 *
 * @param <M> Mapper 类型
 * @param <T> Entity 类型
 * @param <B> BO 类型
 * @param <V> VO 类型
 */
public abstract class BaseProtocolServiceImpl<M extends BaseMapperPlus<M, T, V>, T, B, V> {

    @Autowired
    protected M baseMapper;

    /**
     * 根据 ID 查询
     */
    public V queryById(Long id) {
        if (id == null) {
            return null;
        }
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询列表
     */
    public TableDataInfo<V> queryPageList(B bo, PageQuery pageQuery) {
        LambdaQueryWrapper<T> lqw = buildQueryWrapper(bo);
        Page<V> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    public List<V> queryList(B bo) {
        LambdaQueryWrapper<T> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 新增
     */
    public Boolean insertByBo(B bo) {
        T entity = convertToEntity(bo);
        validEntityBeforeSave(entity);
        boolean flag = baseMapper.insert(entity) > 0;
        if (flag) {
            Long id = (Long) BeanUtil.getProperty(entity, "id");
            if (id != null) {
                ReflectUtil.setFieldValue(bo, "id", id);
            }
        }
        return flag;
    }

    /**
     * 修改
     */
    public Boolean updateByBo(B bo) {
        T entity = convertToEntity(bo);
        validEntityBeforeSave(entity);
        return baseMapper.updateById(entity) > 0;
    }

    /**
     * 校验并批量删除
     */
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            doValidBeforeDelete(ids);
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 保存前的数据校验，子类可重写
     */
    protected void validEntityBeforeSave(T entity) {
        // 默认不做校验
    }

    /**
     * 删除前的业务校验，子类可重写
     */
    protected void doValidBeforeDelete(Collection<Long> ids) {
        // 默认不做校验
    }

    /**
     * 构建查询条件，子类实现
     */
    protected abstract LambdaQueryWrapper<T> buildQueryWrapper(B bo);

    /**
     * BO 转 Entity，子类实现
     */
    protected abstract T convertToEntity(B bo);
}
