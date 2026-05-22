package com.ruoyi.web.controller.zm;

import com.ruoyi.cache.SceneCache;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.bo.DevBaseSceneBo;
import com.ruoyi.zm.domain.vo.DevBaseSceneSelectListVo;
import com.ruoyi.zm.domain.vo.DevBaseSceneVo;
import com.ruoyi.zm.mapper.DevBaseSceneMapper;
import com.ruoyi.zm.service.IDevBaseSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 场景
 *
 * @author mophi
 * @date 2024-07-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/zm/baseScene")
public class DevBaseSceneController extends BaseController {

    private final IDevBaseSceneService iDevBaseSceneService;
    private final DevBaseSceneMapper sceneMapper;
    private final SceneCache sceneCache;

    /**
     * 查询场景列表
     */
    // @SaCheckPermission("zm:baseScene:list")
    @PostMapping("/list")
    public TableDataInfo<DevBaseSceneVo> list(DevBaseSceneBo bo, @RequestBody PageQuery pageQuery) {
        return iDevBaseSceneService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出场景列表
     */
    // @SaCheckPermission("zm:baseScene:export")
    // @Log(title = "场景", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DevBaseSceneBo bo, HttpServletResponse response) {
        List<DevBaseSceneVo> list = iDevBaseSceneService.queryList(bo);
        ExcelUtil.exportExcel(list, "场景", DevBaseSceneVo.class, response);
    }

    /**
     * 获取场景详细信息
     *
     * @param id 主键
     */
    // @SaCheckPermission("zm:baseScene:query")
    @GetMapping("/{id}")
    public R<DevBaseSceneVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDevBaseSceneService.queryById(id));
    }

    /**
     * 新增场景
     */
    // @SaCheckPermission("zm:baseScene:add")
    // @Log(title = "场景", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/add")
    public R<Void> add(@RequestBody DevBaseSceneBo bo) {
        // if (bo.getSceneId() <= 0 || bo.getSceneId() > 10) return R.fail("场景编号范围应在 1 ~ 10 以内");
        return toAjax(iDevBaseSceneService.insertByBo(bo));
    }

    /**
     * 修改场景
     */
    // @SaCheckPermission("zm:baseScene:edit")
    // @Log(title = "场景", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody DevBaseSceneBo bo) {
        // if (bo.getSceneId() <= 0 || bo.getSceneId() > 10) return R.fail("场景编号范围应在 1 ~ 10 以内");
        return toAjax(iDevBaseSceneService.updateByBo(bo));
    }

    /**
     * 删除场景
     *
     * @param ids 主键串
     */
    // @SaCheckPermission("zm:baseScene:remove")
    // @Log(title = "场景", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Transactional
    public R<Void> remove(@NotEmpty(message = "序号不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDevBaseSceneService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    // 直流机柜-机柜设置-场景设置-场景选择的列表
    @GetMapping("/getSceneSelectList")
    public R<List<DevBaseSceneSelectListVo>> getSceneSelectList(Integer deviceId) {
        try {
            return sceneCache.getSceneSelectList(deviceId);
        } catch (Exception e) {
            return R.ok();
        }
    }
}
