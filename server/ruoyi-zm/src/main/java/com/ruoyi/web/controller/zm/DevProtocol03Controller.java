package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevProtocol03Bo;
import com.ruoyi.zm.domain.vo.DevProtocol03Vo;
import com.ruoyi.zm.service.IDevProtocol03Service;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 遥测协议
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/protocol03")
public class DevProtocol03Controller extends BaseController {

    private final IDevProtocol03Service iDevProtocol03Service;

    /**
     * 查询遥测协议列表
     */
    @SaCheckPermission("zm:protocol03:list")
    @PostMapping("/list")
    public TableDataInfo<DevProtocol03Vo> list(DevProtocol03Bo bo,@RequestBody  PageQuery pageQuery) {
        return iDevProtocol03Service.queryPageList(bo, pageQuery);
    }

    /**
     * 导出遥测协议列表
     */
    @SaCheckPermission("zm:protocol03:export")
    // @Log(title = "遥测协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevProtocol03Bo bo, HttpServletResponse response) {
        List<DevProtocol03Vo> list = iDevProtocol03Service.queryList(bo);
        ExcelUtil.exportExcel(list, "遥测协议", DevProtocol03Vo.class, response);
    }

    /**
     * 获取遥测协议详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("zm:protocol03:query")
    @GetMapping("/{id}")
    public R<DevProtocol03Vo> getInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable Long id) {
        return R.ok(iDevProtocol03Service.queryById(id));
    }

    /**
     * 新增遥测协议
     */
    @SaCheckPermission("zm:protocol03:add")
    // @Log(title = "遥测协议", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevProtocol03Bo bo) {
        return toAjax(iDevProtocol03Service.insertByBo(bo));
    }

    /**
     * 修改遥测协议
     */
    @SaCheckPermission("zm:protocol03:edit")
    // @Log(title = "遥测协议", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevProtocol03Bo bo) {
        return toAjax(iDevProtocol03Service.updateByBo(bo));
    }

    /**
     * 删除遥测协议
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zm:protocol03:remove")
    // @Log(title = "遥测协议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevProtocol03Service.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
