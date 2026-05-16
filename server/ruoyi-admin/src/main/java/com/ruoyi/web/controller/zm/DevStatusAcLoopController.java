package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevStatusAcLoopBo;
import com.ruoyi.zm.domain.vo.DevStatusAcLoopVo;
import com.ruoyi.zm.service.IDevStatusAcLoopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 交流回路
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/statusAcLoop")
public class DevStatusAcLoopController extends BaseController {

    private final IDevStatusAcLoopService iDevStatusAcLoopService;

    /**
     * 查询交流回路列表
     */
    // @SaCheckPermission("zm:statusAcLoop:list")
    @PostMapping("/list")
    public TableDataInfo<DevStatusAcLoopVo> list(DevStatusAcLoopBo bo,@RequestBody  PageQuery pageQuery) {
        return iDevStatusAcLoopService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出交流回路列表
     */
    // @SaCheckPermission("zm:statusAcLoop:export")
    // @Log(title = "交流回路", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevStatusAcLoopBo bo, HttpServletResponse response) {
        List<DevStatusAcLoopVo> list = iDevStatusAcLoopService.queryList(bo);
        ExcelUtil.exportExcel(list, "交流回路", DevStatusAcLoopVo.class, response);
    }

    /**
     * 获取交流回路详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:statusAcLoop:query")
    @GetMapping("/{id}")
    public R<DevStatusAcLoopVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevStatusAcLoopService.queryById(id));
    }

    /**
     * 新增交流回路
     */
    // @SaCheckPermission("zm:statusAcLoop:add")
    // @Log(title = "交流回路", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevStatusAcLoopBo bo) {
        return toAjax(iDevStatusAcLoopService.insertByBo(bo));
    }

    /**
     * 修改交流回路
     */
    // @SaCheckPermission("zm:statusAcLoop:edit")
    // @Log(title = "交流回路", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevStatusAcLoopBo bo) {
        return toAjax(iDevStatusAcLoopService.updateByBo(bo));
    }

    /**
     * 删除交流回路
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:statusAcLoop:remove")
    // @Log(title = "交流回路", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevStatusAcLoopService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
