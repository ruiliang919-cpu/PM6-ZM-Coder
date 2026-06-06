package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaCheckPermission;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.cache.Key;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.DevBaseDistrict;
import com.ruoyi.zm.domain.bo.DevBaseDistrictBo;
import com.ruoyi.zm.domain.vo.DevBaseDistrictVo;
import com.ruoyi.zm.domain.vo.PageQueryByName;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.service.IDevBaseDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 控制分区
 *
 * @author mophi
 * @date 2024-07-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/baseDistrict")
public class DevBaseDistrictController extends BaseController {

    private final IDevBaseDistrictService iDevBaseDistrictService;
    private final DevBaseDistrictMapper districtMapper;
    private final Key key;

    /**
     * 查询控制分区列表
     */
    @SaCheckPermission("zm:baseDistrict:list")
    @PostMapping("/list")
    public TableDataInfo<DevBaseDistrictVo> list(@RequestBody PageQueryByName pageQuery) {
        try {
            DevBaseDistrictBo bo = new DevBaseDistrictBo();
            if (StrUtil.isNotBlank(pageQuery.getName())) {
                bo.setName(pageQuery.getName());
            }
            List<DevBaseDistrictVo> data = iDevBaseDistrictService.queryList(bo);
            AtomicInteger no = new AtomicInteger(1);
            data.forEach(item -> item.setOrderNo(no.getAndIncrement()));
            return key.getPageTable(data, pageQuery.getPageNum(), pageQuery.getPageSize());
        } catch (Exception e) {
            return TableDataInfo.build();
        }
    }

    /**
     * 导出控制分区列表
     */
    @SaCheckPermission("zm:baseDistrict:export")
    @PostMapping("/export")
    public void export(DevBaseDistrictBo bo, HttpServletResponse response) {
        List<DevBaseDistrictVo> list = iDevBaseDistrictService.queryList(bo);
        ExcelUtil.exportExcel(list, "控制分区", DevBaseDistrictVo.class, response);
    }

    /**
     * 获取控制分区详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zm:baseDistrict:query")
    @GetMapping("/{id}")
    public R<DevBaseDistrict> getInfo(@NotNull(message = "主键不能为空")
                                          @PathVariable Long id) {
        return R.ok(districtMapper.selectList().get(Math.toIntExact(id - 1)));
    }

    /**
     * 新增控制分区
     */
    @SaCheckPermission("zm:baseDistrict:add")
    @RepeatSubmit()
    @PostMapping("/add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevBaseDistrictBo bo) {
        if (bo.getId() == null || bo.getId() < 0 || bo.getId() > 32) return R.fail("分区码范围在0~31，添加失败");
        if (bo.getName() == null || bo.getName().isEmpty()) return R.warn("分区名称不能留空，修改失败");
        return toAjax(iDevBaseDistrictService.insertByBo(bo));
    }

    /**
     * 修改控制分区
     */
    @SaCheckPermission("zm:baseDistrict:edit")
    @RepeatSubmit()
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody DevBaseDistrictBo bo) {
        if (bo.getId() < 0 || bo.getId() > 32) return R.fail("分区码范围在0~31，修改失败");
        if (bo.getName() == null || bo.getName().isEmpty()) return R.warn("分区名称不能留空，修改失败");
        return toAjax(iDevBaseDistrictService.updateByBo(bo));
    }

    /**
     * 删除控制分区
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zm:baseDistrict:remove")
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevBaseDistrictService.deleteWithValidByIds(ids));
    }
}
