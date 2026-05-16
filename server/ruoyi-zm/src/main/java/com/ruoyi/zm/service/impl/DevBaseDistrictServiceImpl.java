package com.ruoyi.zm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.DevBaseDistrictAssist;
import com.ruoyi.zm.domain.bo.DevBaseDistrictBo;
import com.ruoyi.zm.domain.vo.DevBaseDistrictVo;
import com.ruoyi.zm.mapper.DevBaseDistrictAssistMapper;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.service.IDevBaseDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制分区Service业务层处理
 *
 * @author mophi
 * @date 2024-07-11
 */
@RequiredArgsConstructor
@Service
public class DevBaseDistrictServiceImpl implements IDevBaseDistrictService {

    private final DevBaseDistrictMapper baseMapper;
    private final DevBaseDistrictAssistMapper districtAssistMapper;

    /**
     * 查询控制分区
     */
    @Override
    public DevBaseDistrict queryById(Long id) {
        LambdaQueryWrapper<DevBaseDistrict> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDistrict::getOrderNo, id);
        return baseMapper.selectOne(lqw);
    }

    /**
     * 查询控制分区列表
     */
    @Override
    public TableDataInfo<DevBaseDistrictVo> queryPageList(DevBaseDistrictBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DevBaseDistrict> lqw = buildQueryWrapper(bo);
        Page<DevBaseDistrictVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询控制分区列表
     */
    @Override
    public List<DevBaseDistrictVo> queryList(DevBaseDistrictBo bo) {
        LambdaQueryWrapper<DevBaseDistrict> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DevBaseDistrict> buildQueryWrapper(DevBaseDistrictBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DevBaseDistrict> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DevBaseDistrict::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增控制分区
     */
    @Override
    @Transactional
    public Boolean insertByBo(DevBaseDistrictBo bo) {
        LambdaQueryWrapper<DevBaseDistrict> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(DevBaseDistrict::getOrderNo);
        lqw.last("LIMIT 1");
        DevBaseDistrict last = baseMapper.selectOne(lqw);
        int lastOrderNo = 1;
        if (!ObjectUtils.isEmpty(last)) {
            lastOrderNo = last.getOrderNo() + 1;
        }
        DevBaseDistrict district = new DevBaseDistrict();
        district.setOrderNo(lastOrderNo);
        district.setId(bo.getId());
        district.setName(bo.getName());
        DevBaseDistrictAssist assist = new DevBaseDistrictAssist();
        assist.setId(Math.toIntExact(district.getId()));
        assist.setSwitchStatus(0);
        assist.setLux(0);
        districtAssistMapper.insertOrUpdate(assist);
        return baseMapper.insert(district) > 0;
    }

    /**
     * 修改控制分区
     */
    @Override
    public Boolean updateByBo(DevBaseDistrictBo bo) {
        DevBaseDistrict update = baseMapper.selectById(bo.getOrderNo());
        update.setId(bo.getId());
        update.setName(bo.getName());
        return baseMapper.updateById(update) > 0;
    }


    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DevBaseDistrict entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除控制分区
     */
    @Override
    @Transactional
    public Boolean deleteWithValidByIds(Long[] ids) {
        LambdaQueryWrapper<DevBaseDistrict> lqw = new LambdaQueryWrapper<>();
        List<DevBaseDistrict> districts = baseMapper.selectList(lqw);
        districts = districts.stream().sorted(Comparator.comparingInt(DevBaseDistrict::getOrderNo)).collect(Collectors.toList());
        List<Integer> idList = new ArrayList<>();
        for (Long id : ids) {
            DevBaseDistrict district = districts.get(Math.toIntExact(id - 1));
            idList.add(district.getOrderNo());
        }
        return baseMapper.deleteBatchIds(idList) > 0;
    }
}
