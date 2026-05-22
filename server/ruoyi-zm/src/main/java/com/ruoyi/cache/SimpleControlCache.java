package com.ruoyi.cache;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigSimpleGroup;
import com.ruoyi.zm.domain.DevConfigTimeControl;
import com.ruoyi.zm.domain.vo.ControlEnabledRespVo;
import com.ruoyi.zm.domain.vo.DevConfigTimeControlRespVo;
import com.ruoyi.zm.domain.vo.SimpleGroupRespVo;
import com.ruoyi.zm.domain.vo.TimeControlReqVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleControlCache {
    public static final String[] SIMPLE_LIST_ADDR_ARR = new String[]{"0XA80A", "0XA853", "0XA89C"};
    private final Key key;
    private final LoopByGroupCache loopByGroupCache;
    private final GroupCache groupCache;

    // 普通时控模式参数列表（包括参数引用）
    public R<List<DevConfigTimeControlRespVo>> getSimpleList(Integer deviceId, Integer controlId) {
        try {
            HashMap<String, Object> map = (HashMap<String, Object>) key.getRemote(deviceId, SIMPLE_LIST_ADDR_ARR[controlId - 1]);
            List<DevConfigTimeControl> data = (List<DevConfigTimeControl>) map.get("data1");
            List<DevConfigTimeControlRespVo> result = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                DevConfigTimeControlRespVo vo = new DevConfigTimeControlRespVo();
                vo.setFrameId(i + 1);
                vo.setControlId(controlId);
                vo.setSwitchStatus(data.get(i).getSwitchStatus());
                vo.setEnabledStatus(data.get(i).getEnabledStatus());
                vo.setLux(data.get(i).getLux());
                vo.setStime(data.get(i).getStime());
                vo.setEtime(data.get(i).getEtime());
                result.add(vo);
            }
            return R.ok(result);
        } catch (Exception e) {
            // log.error("SimpleControlCache → getSimpleList", e);
        }
        return null;
    }

    // 机柜设置-时控模式-普通时控模式参数-分组选择
    public TableDataInfo<?> simpleGroupList(TimeControlReqVo vo) {
        try {
            List<String> groupNames = groupCache.groupNames(vo.getDeviceId());
            LinkedList<SimpleGroupRespVo> source = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                SimpleGroupRespVo respVo = new SimpleGroupRespVo();
                respVo.setGroupId(i + 1);
                respVo.setGroupName(groupNames.get(i));
                source.add(respVo);
            }

            try {
                HashMap<String, Object> map = (HashMap<String, Object>) key.getRemote(vo.getDeviceId(), SIMPLE_LIST_ADDR_ARR[vo.getControlId() - 1]);
                List<DevConfigSimpleGroup> data = (List<DevConfigSimpleGroup>) map.get("data2");
                for (int i = 0; i < source.size(); i++) {
                    source.get(i).setSelectStatus(data.get(i).getSelectStatus());
                }
            } catch (Exception e) {
                for (SimpleGroupRespVo simpleGroupRespVo : source) {
                    simpleGroupRespVo.setSelectStatus(0);
                }
            }
            return key.getPageTable(source, 1, 16);
        } catch (Exception e) {
             log.error("SimpleControlCache → simpleGroupList", e);
        }
        return null;
    }

    // 普通时控模式启用状态
    public R<ControlEnabledRespVo> getSimpleEnabled(Integer deviceId, Integer controlId) {
        try {
            String addr = "0XA80AEnabled";
            Map<String, Integer> m = (Map<String, Integer>) key.getRemote(deviceId, addr);
            ControlEnabledRespVo vo = new ControlEnabledRespVo();
            vo.setDeviceId(deviceId);
            vo.setControlId(controlId);
            vo.setEnabled(Objects.equals(m.getOrDefault("data3", 0), controlId));
            return R.ok(vo);
        } catch (Exception e) {
            // log.error("SimpleControlCache → getSimpleEnabled", e);
        }
        return null;
    }
}
