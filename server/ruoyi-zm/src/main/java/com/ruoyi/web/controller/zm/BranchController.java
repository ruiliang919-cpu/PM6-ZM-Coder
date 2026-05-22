package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevBaseBranch;
import com.ruoyi.zm.domain.DevBaseBranchInsulation;
import com.ruoyi.zm.domain.bo.DevStatusBranchBo;
import com.ruoyi.zm.domain.vo.BranchReqVo;
import com.ruoyi.zm.domain.vo.BranchRespVo;
import com.ruoyi.zm.domain.vo.DevStatusBranchVo;
import com.ruoyi.zm.mapper.DevBaseBranchInsulationMapper;
import com.ruoyi.zm.mapper.DevBaseBranchMapper;
import com.ruoyi.zm.service.IDevStatusBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

// 馈线支路的控制层接口
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/branch")
public class BranchController {
    private final DevBaseBranchMapper branchManager;
    private final IDevStatusBranchService statusBranchService;
    private final DevBaseBranchInsulationMapper branchInsulationMapper;

    // 查询馈线支路信息列表
    @PostMapping("/getList")
    public TableDataInfo<BranchRespVo> getList(@RequestBody BranchReqVo vo) {
        DevStatusBranchBo bo = new DevStatusBranchBo();
        bo.setDeviceId(Long.valueOf(vo.getSlaveId()));
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(vo.getPageNum());
        pageQuery.setPageSize(vo.getPageSize());
        List<BranchRespVo> result = new LinkedList<>();
        TableDataInfo<DevStatusBranchVo> source = statusBranchService.queryPageList(bo, pageQuery);
        source.getRows().forEach((item) -> {
            LambdaQueryWrapper<DevBaseBranch> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DevBaseBranch::getId, item.getId());
            lqw.eq(DevBaseBranch::getBranchNo, item.getBranchNo());
            DevBaseBranch branch = branchManager.selectOne(lqw);
            BranchRespVo respVo = new BranchRespVo();
            respVo.setId(item.getId());
            respVo.setBranchNo(item.getBranchNo());
            respVo.setDeviceId(item.getDeviceId());
            respVo.setSwitchStatus(item.getSwitchStatus());
            respVo.setSwitchFault(item.getSwitchFault());
            respVo.setBranchName(branch.getBranchName());
            LambdaQueryWrapper<DevBaseBranchInsulation> abc = new LambdaQueryWrapper<>();
            abc.eq(DevBaseBranchInsulation::getId, item.getId());
            abc.eq(DevBaseBranchInsulation::getBranchNo, item.getBranchNo());
            DevBaseBranchInsulation devBaseBranchInsulation = branchInsulationMapper.selectOne(abc);
            respVo.setNegativeBranchInsulation(Math.toIntExact(devBaseBranchInsulation.getNegativeBranchInsulation()));
            respVo.setPositiveBranchInsulation(Math.toIntExact(devBaseBranchInsulation.getPositiveBranchInsulation()));
            result.add(respVo);
        });
        return TableDataInfo.build(result);
    }
}
