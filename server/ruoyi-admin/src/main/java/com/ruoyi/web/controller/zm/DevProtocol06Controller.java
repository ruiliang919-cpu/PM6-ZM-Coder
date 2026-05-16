package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevProtocol06Bo;
import com.ruoyi.zm.domain.vo.DevProtocol06Vo;
import com.ruoyi.zm.service.IDevProtocol06Service;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 遥调协议
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/protocol06")
public class DevProtocol06Controller extends BaseController {

    private final IDevProtocol06Service iDevProtocol06Service;

    /**
     * 查询遥调协议列表
     */
    // @SaCheckPermission("zm:protocol06:list")
    @PostMapping("/list")
    public TableDataInfo<DevProtocol06Vo> list(DevProtocol06Bo bo,@RequestBody  PageQuery pageQuery) {
        return iDevProtocol06Service.queryPageList(bo, pageQuery);
    }

    /**
     * 导出遥调协议列表
     */
    // @SaCheckPermission("zm:protocol06:export")
    // @Log(title = "遥调协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevProtocol06Bo bo, HttpServletResponse response) {
        List<DevProtocol06Vo> list = iDevProtocol06Service.queryList(bo);
        ExcelUtil.exportExcel(list, "遥调协议", DevProtocol06Vo.class, response);
    }

    /**
     * 获取遥调协议详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:protocol06:query")
    @GetMapping("/{id}")
    public R<DevProtocol06Vo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevProtocol06Service.queryById(id));
    }

    /**
     * 新增遥调协议
     */
    // @SaCheckPermission("zm:protocol06:add")
    // @Log(title = "遥调协议", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevProtocol06Bo bo) {
        return toAjax(iDevProtocol06Service.insertByBo(bo));
    }

    /**
     * 修改遥调协议
     */
    // @SaCheckPermission("zm:protocol06:edit")
    // @Log(title = "遥调协议", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevProtocol06Bo bo) {
        return toAjax(iDevProtocol06Service.updateByBo(bo));
    }

    /**
     * 删除遥调协议
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:protocol06:remove")
    // @Log(title = "遥调协议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevProtocol06Service.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
