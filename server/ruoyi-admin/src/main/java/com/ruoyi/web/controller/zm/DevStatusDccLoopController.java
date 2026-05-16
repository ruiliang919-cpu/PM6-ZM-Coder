package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevStatusDccLoopBo;
import com.ruoyi.zm.domain.vo.DevStatusDccLoopVo;
import com.ruoyi.zm.service.IDevStatusDccLoopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 直流回路
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/statusDccLoop")
public class DevStatusDccLoopController extends BaseController {

    private final IDevStatusDccLoopService iDevStatusDccLoopService;

    /**
     * 查询直流回路列表
     */
    // @SaCheckPermission("zm:statusDccLoop:list")
    @PostMapping("/list")
    public TableDataInfo<DevStatusDccLoopVo> list(DevStatusDccLoopBo bo,@RequestBody  PageQuery pageQuery) {
        return iDevStatusDccLoopService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出直流回路列表
     */
    // @SaCheckPermission("zm:statusDccLoop:export")
    // @Log(title = "直流回路", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevStatusDccLoopBo bo, HttpServletResponse response) {
        List<DevStatusDccLoopVo> list = iDevStatusDccLoopService.queryList(bo);
        ExcelUtil.exportExcel(list, "直流回路", DevStatusDccLoopVo.class, response);
    }

    /**
     * 获取直流回路详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:statusDccLoop:query")
    @GetMapping("/{id}")
    public R<DevStatusDccLoopVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevStatusDccLoopService.queryById(id));
    }

    /**
     * 新增直流回路
     */
    // @SaCheckPermission("zm:statusDccLoop:add")
    // @Log(title = "直流回路", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevStatusDccLoopBo bo) {
        return toAjax(iDevStatusDccLoopService.insertByBo(bo));
    }

    /**
     * 修改直流回路
     */
    // @SaCheckPermission("zm:statusDccLoop:edit")
    // @Log(title = "直流回路", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevStatusDccLoopBo bo) {
        return toAjax(iDevStatusDccLoopService.updateByBo(bo));
    }

    /**
     * 删除直流回路
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:statusDccLoop:remove")
    // @Log(title = "直流回路", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevStatusDccLoopService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
