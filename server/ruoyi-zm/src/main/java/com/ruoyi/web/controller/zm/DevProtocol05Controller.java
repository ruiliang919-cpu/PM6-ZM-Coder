package com.ruoyi.web.controller.zm;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevProtocol05Bo;
import com.ruoyi.zm.domain.vo.DevProtocol05Vo;
import com.ruoyi.zm.service.IDevProtocol05Service;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 遥控协议
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/protocol05")
public class DevProtocol05Controller extends BaseController {

    private final IDevProtocol05Service iDevProtocol05Service;

    /**
     * 查询遥控协议列表
     */
    // @SaCheckPermission("zm:protocol05:list")
    @PostMapping("/list")
    public TableDataInfo<DevProtocol05Vo> list(DevProtocol05Bo bo,@RequestBody  PageQuery pageQuery) {
        return iDevProtocol05Service.queryPageList(bo, pageQuery);
    }

    /**
     * 导出遥控协议列表
     */
    // @SaCheckPermission("zm:protocol05:export")
    // @Log(title = "遥控协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevProtocol05Bo bo, HttpServletResponse response) {
        List<DevProtocol05Vo> list = iDevProtocol05Service.queryList(bo);
        ExcelUtil.exportExcel(list, "遥控协议", DevProtocol05Vo.class, response);
    }

    /**
     * 获取遥控协议详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:protocol05:query")
    @GetMapping("/{id}")
    public R<DevProtocol05Vo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevProtocol05Service.queryById(id));
    }

    /**
     * 新增遥控协议
     */
    // @SaCheckPermission("zm:protocol05:add")
    // @Log(title = "遥控协议", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DevProtocol05Bo bo) {
        return toAjax(iDevProtocol05Service.insertByBo(bo));
    }

    /**
     * 修改遥控协议
     */
    // @SaCheckPermission("zm:protocol05:edit")
    // @Log(title = "遥控协议", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DevProtocol05Bo bo) {
        return toAjax(iDevProtocol05Service.updateByBo(bo));
    }

    /**
     * 删除遥控协议
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:protocol05:remove")
    // @Log(title = "遥控协议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevProtocol05Service.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
