package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.GroupCache;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.LoopByGroupCache;
import com.ruoyi.cache.SceneCache;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.DevConfigDistrict;
import com.ruoyi.zm.domain.DevGroupLoop;
import com.ruoyi.zm.domain.bo.DevConfigGroupBo;
import com.ruoyi.zm.domain.bo.DevConfigSceneBo;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.*;
import com.ruoyi.zm.service.IDevConfigGroupService;
import com.ruoyi.zm.service.IDevConfigSceneService;
import com.ruoyi.zm.service.IDevStatusDccLoopService;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 回路分组
 *
 * @author ruoyi
 * @date 2024-08-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/configGroup")
public class DevConfigGroupController extends BaseController {

    private final IDevConfigGroupService iDevConfigGroupService;
    private final IDevConfigSceneService configSceneService;
    private final IDevStatusDccLoopService dccLoopService;
    private final DevGroupLoopMapper groupLoopMapper;
    private final DevStatusDccLoopMapper dccLoopMapper;
    private final DevConfigGroupMapper groupMapper;
    private final IDevConfigGroupService groupService;
    private final DevConfigDistrictMapper configDistrictMapper;
    private final DevBaseDistrictMapper baseDistrictMapper;
    private final LoopByGroupCache loopByGroupCache;
    private final SceneCache sceneCache;
    private final GroupCache groupCache;
    private final DevBaseDistrictMapper districtMapper;
    private final Key key;

    private static final List<Integer> loopSource = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5,
        6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
        28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40));

    /**
     * 机柜设置-回路分组-分区下拉列表
     */
    @GetMapping("/getDistrictList")
    public R<List<GroupDistrictRespVo>> getDistrictList() {
        List<GroupDistrictRespVo> result = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            GroupDistrictRespVo vo = new GroupDistrictRespVo();
            vo.setId(Math.toIntExact(i));
            vo.setName("分区码：" + vo.getId());
            result.add(vo);
        }
        return R.ok(result);
    }

    /**
     * 查询回路分组列表
     */
    @SaCheckPermission("zm:configGroup:list")
    @PostMapping("/list")
    public TableDataInfo<DevConfigGroupVo> list(Integer deviceId, @RequestBody PageQuery pageQuery) {
        TableDataInfo<DevConfigGroupVo> groupList = loopByGroupCache.groupList(deviceId, pageQuery);
        if (groupList != null) return groupList;
        List<DevConfigGroupVo> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            DevConfigGroupVo vo = new DevConfigGroupVo();
            vo.setId((long) (i + 1));
            vo.setName("分组" + (i + 1));
            vo.setDeviceId(Long.valueOf(deviceId));
            vo.setLux(0);
            vo.setLoopNum(0L);
            vo.setDistrictAddr(0);
            vo.setGroupId((long) (i + 1));
            vo.setSelectStatus(0);
            result.add(vo);
        }
        return key.getPageTable(result, pageQuery);
    }

    /**
     * 机柜设置-回路分组-分区列表，根据设备ID与分组ID得到当前分组的分区
     */
    @GetMapping("/getDistrictByGroupId")
    public R<GroupDistrictRespVo> getDistrictByGroupId(Integer deviceId, Integer groupId) {
        R<GroupDistrictRespVo> result = loopByGroupCache.getDistrictByGroupId(deviceId, groupId);
        if (result != null) return result;
        LambdaQueryWrapper<DevConfigDistrict> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevConfigDistrict::getDeviceId, deviceId);
        lqw.eq(DevConfigDistrict::getDistrictId, groupId);
        DevConfigDistrict devConfigDistrict = configDistrictMapper.selectOne(lqw);
        if (devConfigDistrict != null && devConfigDistrict.getAddr() != null && devConfigDistrict.getAddr() != 0) {
            GroupDistrictRespVo vo = new GroupDistrictRespVo();
            vo.setId(devConfigDistrict.getAddr());
            vo.setName("分区码：" + vo.getId());
            return R.ok(vo);
        }
        return R.ok();
    }

    public static List<Integer> padList(String input) {
        List<Integer> result = new ArrayList<>();
        if (!input.equals("null")) {
            IntStream.range(0, input.length() / 2)
                .mapToObj(i -> input.substring(i * 2, i * 2 + 2))
                .forEach(item -> {
                    try {
                        int no = Integer.parseInt(item);
                        result.add(no);
                    } catch (Exception ignored) {

                    }
                });
        }
        return result;
    }

    /**
     * 根据分组ID查询对应的回路
     */
    @SaCheckPermission("zm:configGroup:getLoopsByGroup")
    @PostMapping("/getLoopsByGroup")
    public R<List<WebDccLoopGroupRespVO>> getLoopsByGroup(@RequestBody WebDccLoopGruopReqVO vo) {
        R<List<WebDccLoopGroupRespVO>> results = loopByGroupCache.getLoopsByGroup(vo);
        if (results != null) return results;
        LambdaQueryWrapper<DevGroupLoop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevGroupLoop::getDeviceId, vo.getSlaveId());
        lqw.eq(DevGroupLoop::getGroupId, vo.getGroupId());
        DevGroupLoop devGroupLoop = groupLoopMapper.selectOne(lqw);
        List<WebDccLoopGroupRespVO> result = new LinkedList<>();
        if (devGroupLoop != null && devGroupLoop.getLoopNo() != null) {
            padList(devGroupLoop.getLoopNo()).forEach(item -> {
                WebDccLoopGroupRespVO respVo = new WebDccLoopGroupRespVO();
                respVo.setNo(item);
                result.add(respVo);
            });
        }
        return R.ok(result);
    }

    /**
     * 根据设备ID查询未被分组的回路
     */
    @SaCheckPermission("zm:configGroup:getLoopsByFree")
    @PostMapping("/getLoopsByFree")
    public R<List<WebDccLoopGroupRespVO>> getLoopsByFree(@RequestBody WebDccLoopGruopReqVO vo) {
        R<List<WebDccLoopGroupRespVO>> results = loopByGroupCache.getLoopsByFree(vo);
        if (results != null) return results;
        LambdaQueryWrapper<DevGroupLoop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevGroupLoop::getDeviceId, vo.getSlaveId());
        lqw.eq(DevGroupLoop::getGroupId, vo.getGroupId());
        DevGroupLoop devGroupLoop = groupLoopMapper.selectOne(lqw);
        List<Integer> source = new ArrayList<>();
        if (devGroupLoop != null && devGroupLoop.getLoopNo() != null) {
            source = padList(devGroupLoop.getLoopNo());
        }
        // 计算差集
        List<Integer> difference = new ArrayList<>(loopSource);
        difference.removeAll(source);
        List<WebDccLoopGroupRespVO> result = new ArrayList<>();
        difference.forEach(item -> {
            WebDccLoopGroupRespVO respVO = new WebDccLoopGroupRespVO();
            respVO.setNo(item);
            result.add(respVO);
        });
        return R.ok(result);
    }

    /**
     * 新增回路到回路分组中
     */
    @SaCheckPermission("zm:configGroup:insertLoopToGroup")
    @GetMapping("/insertLoopToGroup")
    public R<Void> insertLoopToGroup(Long salveId, Long groupId, Long loopId) throws ModbusTransportException {

        return R.ok();
    }

    /**
     * 从回路分组中删除回路
     */
    @SaCheckPermission("zm:configGroup:deleteLoopFromGroup")
    @GetMapping("/deleteLoopFromGroup")
    public R<Void> deleteLoopFromGroup(Long salveId, Long groupId, Long loopId) throws ModbusTransportException {

        return R.ok();
    }

    /**
     * 导出回路分组列表
     */
    @SaCheckPermission("zm:configGroup:export")
    // @Log(title = "回路分组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevConfigGroupBo bo, HttpServletResponse response) {
        List<DevConfigGroupVo> list = iDevConfigGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "回路分组", DevConfigGroupVo.class, response);
    }

    /**
     * 获取回路分组详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zm:configGroup:query")
    @GetMapping("/{id}")
    public R<DevConfigGroupVo> getInfo(@NotNull(message = "主键不能为空")
                                       @PathVariable Long id) {
        return R.ok(iDevConfigGroupService.queryById(id));
    }

    /**
     * 新增回路分组
     */
    @SaCheckPermission("zm:configGroup:add")
    // @Log(title = "回路分组", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevConfigGroupBo bo) {
        return toAjax(iDevConfigGroupService.insertByBo(bo));
    }

    /**
     * 修改回路分组
     */
    @SaCheckPermission("zm:configGroup:edit")
    // @Log(title = "回路分组", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevConfigGroupBo bo) {
        return toAjax(iDevConfigGroupService.updateByBo(bo));
    }

    /**
     * 删除回路分组
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zm:configGroup:remove")
    // @Log(title = "回路分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevConfigGroupService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 机柜设置-场景设置-场景参数 根据设备ID与场景ID查询场景分组配置信息（分页）
     */
    @PostMapping("/getSceneParamsList")
    public TableDataInfo<SceneParamsListRespVo> getSceneParamsList(@RequestBody SceneParamsListReqVo reqVo) {
        TableDataInfo<SceneParamsListRespVo> sceneParamsList = sceneCache.getSceneParamsList(reqVo);
        if (sceneParamsList != null) return sceneParamsList;
        DevConfigSceneBo sceneBo = new DevConfigSceneBo();
        sceneBo.setDeviceId(Long.valueOf(reqVo.getDeviceId()));
        sceneBo.setSceneId(Long.valueOf(reqVo.getSceneId()));
        DevConfigGroupBo groupBo = new DevConfigGroupBo();
        groupBo.setDeviceId(Long.valueOf(reqVo.getDeviceId()));
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(16);
        pageQuery.setPageNum(reqVo.getPageNum());

        List<DevConfigSceneVo> queryList1 = configSceneService.queryList(sceneBo);
        List<DevConfigGroupVo> queryList2 = groupService.queryList(groupBo);

        if (queryList1.size() < 16 || queryList2.size() < 16) {
            return TableDataInfo.build();
        }

        List<DevConfigSceneVo> source1 = configSceneService.queryPageList(sceneBo, pageQuery).getRows();
        List<DevConfigGroupVo> source2 = groupService.queryPageList(groupBo, pageQuery).getRows();

        LinkedList<SceneParamsListRespVo> result = new LinkedList<>();

        source1.forEach(item -> {
            // System.out.println(item);
            SceneParamsListRespVo vo = new SceneParamsListRespVo();
            vo.setGroupId(Math.toIntExact(item.getGroupId()));
            vo.setBtnStatus(item.getBtnStatus());
            vo.setLux(item.getLux());
            vo.setSelectStatus(item.getSelectStatus());
            result.add(vo);
        });

        for (int i = 0; i < source1.size(); i++) {
            result.get(i).setGroupName(source2.get(i).getName());
        }

        return TableDataInfo.build(result);
    }

    // 获取单个组的名称
    @GetMapping("/getGroupName")
    public R<String> getGroupName(Integer deviceId, Integer groupId) {
        return R.ok("操作成功", groupCache.getGroupName(deviceId, groupId));
    }

    // 获取所有分区的名称（不去重）
    @GetMapping("/getDistrictNames")
    public R<List<DistrictNameVo>> getDistrictNames() {
        List<DevBaseDistrict> devBaseDistricts = districtMapper.selectList();
        List<DistrictNameVo> result = new ArrayList<>();
        AtomicInteger id = new AtomicInteger();
        devBaseDistricts.stream()
            // .filter(Key.distinctByKey(DevBaseDistrict::getName))
            .forEach(item -> {
                DistrictNameVo vo = new DistrictNameVo();
                vo.setId(item.getOrderNo());
                vo.setName(item.getName());
                result.add(vo);
            });
        return R.ok(result);
    }
}
