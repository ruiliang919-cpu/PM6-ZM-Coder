package com.ruoyi.zm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevProtocol05;
import com.ruoyi.zm.domain.bo.DevProtocol05Bo;
import com.ruoyi.zm.domain.vo.DevProtocol05Vo;
import com.ruoyi.zm.mapper.DevProtocol05Mapper;
import com.ruoyi.zm.service.IDevProtocol05Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 遥调协议Service业务层处理
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class DevProtocol05ServiceImpl implements IDevProtocol05Service {

    private final DevProtocol05Mapper baseMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public List<DevProtocol05> get05List() {
        List<DevProtocol05> devProtocol05s = (List<DevProtocol05>) redisTemplate.opsForValue().get("zm:devProtocol05sList");
        if (ObjectUtils.isEmpty(devProtocol05s)) {
            devProtocol05s = baseMapper.selectList();
            redisTemplate.opsForValue().set("zm:devProtocol05sList", devProtocol05s, 2, TimeUnit.DAYS);
        }
        // System.out.println("devProtocol05s:"+devProtocol05s);
        return devProtocol05s;
    }

    /**
     * 查询遥调协议
     */
    @Override
    public DevProtocol05Vo queryById(Long id){
        //return baseMapper.selectVoById(id);
        return null;
    }

    /**
     * 查询遥调协议列表
     */
    @Override
    public TableDataInfo<DevProtocol05Vo> queryPageList(DevProtocol05Bo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevProtocol05> lqw = buildQueryWrapper(bo);
        Page<DevProtocol05Vo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询遥调协议列表
     */
    @Override
    public List<DevProtocol05Vo> queryList(DevProtocol05Bo bo) {
        LambdaQueryWrapper<DevProtocol05> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevProtocol05> buildQueryWrapper(DevProtocol05Bo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevProtocol05> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAddr()), DevProtocol05::getAddr, bo.getAddr());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevProtocol05::getName, bo.getName());
        lqw.eq(bo.getMagnification() != null, DevProtocol05::getMagnification, bo.getMagnification());
        lqw.eq(StringUtils.isNotBlank(bo.getUnit()), DevProtocol05::getUnit, bo.getUnit());
        lqw.eq(StringUtils.isNotBlank(bo.getReadWrite()), DevProtocol05::getReadWrite, bo.getReadWrite());
        lqw.eq(StringUtils.isNotBlank(bo.getMemo()), DevProtocol05::getMemo, bo.getMemo());
        return lqw;
    }

    /**
     * 新增遥调协议
     */
    @Override
    public Boolean insertByBo(DevProtocol05Bo bo) {
        DevProtocol05 add = BeanUtil.toBean(bo, DevProtocol05.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改遥调协议
     */
    @Override
    public Boolean updateByBo(DevProtocol05Bo bo) {
        DevProtocol05 update = BeanUtil.toBean(bo, DevProtocol05.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevProtocol05 entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除遥调协议
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    // 根据遥测协议的地址找到对应的名称
    public DevProtocol05 getByProtocolAddr(String addr) {
        DevProtocol05 devProtocol05 = (DevProtocol05) redisTemplate.opsForValue().get("zm:devProtocol05:addr:" + addr);
        if (ObjectUtils.isEmpty(devProtocol05)) {
            Optional<DevProtocol05> optionalProtocol = get05List().stream()
                .filter(value -> value.getAddr().equals(addr))
                .findFirst();
            if (optionalProtocol.isPresent()) {
                redisTemplate.opsForValue().set("zm:devProtocol05:addr:" + addr, optionalProtocol.get(), 2, TimeUnit.DAYS);
                return optionalProtocol.get();
            } else {
                return new DevProtocol05();
            }
        }
        return devProtocol05;
    }
}
