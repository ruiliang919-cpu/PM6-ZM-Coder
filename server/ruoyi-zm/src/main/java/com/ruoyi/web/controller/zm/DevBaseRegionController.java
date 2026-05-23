package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.dtflys.forest.utils.StringUtils;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevBaseRegionBo;
import com.ruoyi.zm.domain.vo.DevBaseRegionVo;
import com.ruoyi.zm.domain.vo.PageQueryByName;
import com.ruoyi.zm.service.IDevBaseRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 设备区域
 *
 * @author ruoyi
 * @date 2024-07-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/baseRegion")
public class DevBaseRegionController extends BaseController {

    private final IDevBaseRegionService iDevBaseRegionService;

    /**
     * 查询设备区域列表
     */
    @SaCheckPermission("zm:baseRegion:list")
    @PostMapping("/list")
    public TableDataInfo<DevBaseRegionVo> list(DevBaseRegionBo bo, @RequestBody PageQueryByName pageQuery) {
        if (StringUtils.isNotBlank(pageQuery.getName())) {
            bo.setName(pageQuery.getName());
        }
        PageQuery pageQuery1 = new PageQuery();
        pageQuery1.setPageNum(pageQuery.getPageNum());
        pageQuery1.setPageSize(pageQuery.getPageSize());
        TableDataInfo<DevBaseRegionVo> result = iDevBaseRegionService.queryPageList(bo, pageQuery1);
        int id = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        id++;
        for (DevBaseRegionVo row : result.getRows()) {
            row.setOrderNo(id++);
        }
        return result;
    }

    /**
     * 查询设备区域列表，不分页
     */
    @SaCheckPermission("zm:baseRegion:noPageList")
    @GetMapping("/noPageList")
    public R<List<DevBaseRegionVo>> noPageList(DevBaseRegionBo bo) {
        return R.ok(iDevBaseRegionService.queryList(bo));
    }

    /**
     * 导出设备区域列表
     */
    @SaCheckPermission("zm:baseRegion:export")
    // @Log(title = "设备区域", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevBaseRegionBo bo, HttpServletResponse response) {
        List<DevBaseRegionVo> list = iDevBaseRegionService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备区域", DevBaseRegionVo.class, response);
    }

    /**
     * 获取设备区域详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zm:baseRegion:query")
    @GetMapping("/{id}")
    public R<DevBaseRegionVo> getInfo(@NotNull(message = "主键不能为空")
                                          @PathVariable Long id) {
        return R.ok(iDevBaseRegionService.queryById(id));
    }

    /**
     * 新增设备区域
     */
    @SaCheckPermission("zm:baseRegion:add")
    // @Log(title = "设备区域", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevBaseRegionBo bo) {
        return toAjax(iDevBaseRegionService.insertByBo(bo));
    }

    /**
     * 修改设备区域
     */
    @SaCheckPermission("zm:baseRegion:edit")
    // @Log(title = "设备区域", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevBaseRegionBo bo) {
        return toAjax(iDevBaseRegionService.updateByBo(bo));
    }

    /**
     * 删除设备区域
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zm:baseRegion:remove")
    // @Log(title = "设备区域", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevBaseRegionService.deleteWithValidByIds(Arrays.asList(ids), false));
    }
}
