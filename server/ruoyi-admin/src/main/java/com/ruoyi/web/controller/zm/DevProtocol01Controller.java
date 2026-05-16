package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevProtocol01Bo;
import com.ruoyi.zm.domain.vo.DevProtocol01Vo;
import com.ruoyi.zm.service.IDevProtocol01Service;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 遥信协议
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/protocol01")
public class DevProtocol01Controller extends BaseController {

    private final IDevProtocol01Service iDevProtocol01Service;

    /**
     * 查询遥信协议列表
     */
    // @SaCheckPermission("zm:protocol01:list")
    @PostMapping("/list")
    public TableDataInfo<DevProtocol01Vo> list(DevProtocol01Bo bo,@RequestBody  PageQuery pageQuery) {
        return iDevProtocol01Service.queryPageList(bo, pageQuery);
    }

    /**
     * 导出遥信协议列表
     */
    // @SaCheckPermission("zm:protocol01:export")
    // @Log(title = "遥信协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevProtocol01Bo bo, HttpServletResponse response) {
        List<DevProtocol01Vo> list = iDevProtocol01Service.queryList(bo);
        ExcelUtil.exportExcel(list, "遥信协议", DevProtocol01Vo.class, response);
    }

    /**
     * 获取遥信协议详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:protocol01:query")
    @GetMapping("/{id}")
    public R<DevProtocol01Vo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevProtocol01Service.queryById(id));
    }

    /**
     * 新增遥信协议
     */
    // @SaCheckPermission("zm:protocol01:add")
    // @Log(title = "遥信协议", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevProtocol01Bo bo) {
        return toAjax(iDevProtocol01Service.insertByBo(bo));
    }

    /**
     * 修改遥信协议
     */
    // @SaCheckPermission("zm:protocol01:edit")
    // @Log(title = "遥信协议", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevProtocol01Bo bo) {
        return toAjax(iDevProtocol01Service.updateByBo(bo));
    }

    /**
     * 删除遥信协议
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:protocol01:remove")
    // @Log(title = "遥信协议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevProtocol01Service.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
