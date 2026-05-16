package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevStatusAcBo;
import com.ruoyi.zm.domain.vo.DevStatusAcVo;
import com.ruoyi.zm.service.IDevStatusAcService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 交流信息
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/statusAc")
public class DevStatusAcController extends BaseController {

    private final IDevStatusAcService iDevStatusAcService;

    /**
     * 查询交流信息列表
     */
    // @SaCheckPermission("zm:statusAc:list")
    @PostMapping("/list")
    public TableDataInfo<DevStatusAcVo> list(DevStatusAcBo bo,@RequestBody  PageQuery pageQuery) {
        return iDevStatusAcService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出交流信息列表
     */
    // @SaCheckPermission("zm:statusAc:export")
    // @Log(title = "交流信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevStatusAcBo bo, HttpServletResponse response) {
        List<DevStatusAcVo> list = iDevStatusAcService.queryList(bo);
        ExcelUtil.exportExcel(list, "交流信息", DevStatusAcVo.class, response);
    }

    /**
     * 获取交流信息详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:statusAc:query")
    @GetMapping("/{id}")
    public R<DevStatusAcVo> getInfo(@NotNull(message = "主键不能为空")
                                    @PathVariable Long id) {
        return R.ok(iDevStatusAcService.queryById(id));
    }

    /**
     * 新增交流信息
     */
    // @SaCheckPermission("zm:statusAc:add")
    // @Log(title = "交流信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevStatusAcBo bo) {
        return toAjax(iDevStatusAcService.insertByBo(bo));
    }

    /**
     * 修改交流信息
     */
    // @SaCheckPermission("zm:statusAc:edit")
    // @Log(title = "交流信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevStatusAcBo bo) {
        return toAjax(iDevStatusAcService.updateByBo(bo));
    }

    /**
     * 删除交流信息
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:statusAc:remove")
    // @Log(title = "交流信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevStatusAcService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
