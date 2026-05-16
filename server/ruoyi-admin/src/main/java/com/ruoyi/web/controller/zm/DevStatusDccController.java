package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevStatusDccBo;
import com.ruoyi.zm.domain.vo.DevStatusDccVo;
import com.ruoyi.zm.service.IDevStatusDccService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 直流信息
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/statusDcc")
public class DevStatusDccController extends BaseController {

    private final IDevStatusDccService iDevStatusDccService;

    /**
     * 查询直流信息列表
     */
    // @SaCheckPermission("zm:statusDcc:list")
    @PostMapping("/list")
    public TableDataInfo<DevStatusDccVo> list(DevStatusDccBo bo,@RequestBody  PageQuery pageQuery) {
        return iDevStatusDccService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出直流信息列表
     */
    // @SaCheckPermission("zm:statusDcc:export")
    // @Log(title = "直流信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevStatusDccBo bo, HttpServletResponse response) {
        List<DevStatusDccVo> list = iDevStatusDccService.queryList(bo);
        ExcelUtil.exportExcel(list, "直流信息", DevStatusDccVo.class, response);
    }

    /**
     * 获取直流信息详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:statusDcc:query")
    @GetMapping("/{id}")
    public R<DevStatusDccVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevStatusDccService.queryById(id));
    }

    /**
     * 新增直流信息
     */
    // @SaCheckPermission("zm:statusDcc:add")
    // @Log(title = "直流信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevStatusDccBo bo) {
        return toAjax(iDevStatusDccService.insertByBo(bo));
    }

    /**
     * 修改直流信息
     */
    // @SaCheckPermission("zm:statusDcc:edit")
    // @Log(title = "直流信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevStatusDccBo bo) {
        return toAjax(iDevStatusDccService.updateByBo(bo));
    }

    /**
     * 删除直流信息
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:statusDcc:remove")
    // @Log(title = "直流信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevStatusDccService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
