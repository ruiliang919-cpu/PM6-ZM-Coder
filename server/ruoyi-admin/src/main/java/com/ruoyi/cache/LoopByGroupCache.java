package com.ruoyi.cache;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.web.controller.zm.DevConfigGroupController;
import com.ruoyi.zm.domain.vo.DevConfigGroupVo;
import com.ruoyi.zm.domain.vo.GroupDistrictRespVo;
import com.ruoyi.zm.domain.vo.WebDccLoopGroupRespVO;
import com.ruoyi.zm.domain.vo.WebDccLoopGruopReqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoopByGroupCache {
    public static final String[] NAMES_ADDR_ARR = new String[]{
        "0xA1F0", "0xA1FA", "0xA204", "0xA20E", "0xA218", "0xA222", "0xA22C", "0xA236",
        "0xA240", "0xA24A", "0xA254", "0xA25E", "0xA268", "0xA272", "0xA27C", "0xA286"};
    public static final String[] LOOP_NUMS_ADDR_ARR = new String[]{
        "0x23F5", "0x241D", "0x2445", "0x246D", "0x2495", "0x24BD", "0x24E5", "0x250D",
        "0x2535", "0x255D", "0x2585", "0x25AD", "0x25D5", "0x25FD", "0x2625", "0x264D"};
    public static final List<Integer> loopSource = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5,
        6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
        28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40));
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;

    // 根据设备ID与分组ID查询未被分组的回路
    public R<List<WebDccLoopGroupRespVO>> getLoopsByFree(WebDccLoopGruopReqVO vo) {
        try {
            List<WebDccLoopGroupRespVO> result = new ArrayList<>();
            String noStr = key.getTelemeter(Math.toIntExact(vo.getSlaveId()), LOOP_NUMS_ADDR_ARR[(int) (vo.getGroupId() - 1)]) + "";
            List<Integer> loopsByGroup = DevConfigGroupController.padList(noStr);
            List<Integer> difference = new ArrayList<>(loopSource);
            difference.removeAll(loopsByGroup);
            difference.forEach(item -> {
                WebDccLoopGroupRespVO respVO = new WebDccLoopGroupRespVO();
                respVO.setNo(item);
                result.add(respVO);
            });
            return R.ok(result);
        } catch (Exception e) {
            log.error("LoopByGroupCache → getLoopsByFree", e);
        }
        return null;
    }

    // 根据设备ID与分组ID查询已被分组的回路
    public R<List<WebDccLoopGroupRespVO>> getLoopsByGroup(WebDccLoopGruopReqVO vo) {
        try {
            List<WebDccLoopGroupRespVO> result = new ArrayList<>();
            String noStr = key.getTelemeter(Math.toIntExact(vo.getSlaveId()), LOOP_NUMS_ADDR_ARR[(int) (vo.getGroupId() - 1)]) + "";
            if ("null".equals(noStr)) {
                return R.ok(result);
            }
            for (int i = 0; i < noStr.length() / 2; i++) {
                WebDccLoopGroupRespVO respVO = new WebDccLoopGroupRespVO();
                respVO.setNo(Integer.valueOf(noStr.substring(i * 2, i * 2 + 2)));
                result.add(respVO);
            }
            return R.ok(result);
        } catch (Exception e) {
            // log.error("LoopByGroupCache → getLoopsByGroup", e);
        }
        return null;
    }

    // 根据分组ID获取分区ID与名称
    public R<GroupDistrictRespVo> getDistrictByGroupId(Integer deviceId, Integer groupId) {
        try {
            short[] codes = key.getRemoteByArr(deviceId, "0xA5AE");
            // System.err.println(Arrays.toString(codes));
            GroupDistrictRespVo vo = new GroupDistrictRespVo();
            if (codes[0] != -1) {
                vo.setId((int) codes[groupId - 1]);
                vo.setName("分区码：" + vo.getId());
            } else {
                vo.setId(1);
                vo.setName("分区码：" + vo.getId());
            }
            return R.ok(vo);
        } catch (Exception e) {
            // log.error("LoopByGroupCache → getDistrictByGroupId", e);
            GroupDistrictRespVo vo = new GroupDistrictRespVo();
            vo.setId(1);
            vo.setName("分区码：" + vo.getId());
            return R.ok(vo);
        }
    }

    private static final List<Long> numsArray = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);

    // 获取回路分组选择列表
    public TableDataInfo<DevConfigGroupVo> groupList(Integer deviceId, PageQuery pageQuery) {
        try {
            String ip = key.getCreateTCP(deviceId).getIp();

            // 分组名称列表
            List<String> names = getGroupNamesByIp(deviceId, ip);
            // 分组回路总数列表
            List<Long> nums = (List<Long>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(ip, "0x23F5num", deviceId));
            if (nums == null || nums.isEmpty()) nums = numsArray;
            // 分组对应分区码列表
            short[] codes = key.getRemoteByArr(deviceId, "0xA5AE");
            if (codes[0] == -1) codes = new short[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
            List<DevConfigGroupVo> source = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                DevConfigGroupVo vo = new DevConfigGroupVo();
                vo.setId((long) (i + 1));
                vo.setDeviceId(Long.valueOf(deviceId));
                vo.setGroupId((long) (i + 1));
                vo.setName(names.get(i));
                vo.setLoopNum(nums.get(i));
                vo.setDistrictAddr((int) codes[i]);
                source.add(vo);
            }
            return key.getPageTable(source, pageQuery);
        } catch (Exception e) {
            // log.error("LoopByGroupCache → groupList", e);
        }
        return null;
    }

    // 获取十六个分组名称
    public List<String> getGroupNames(Integer deviceId) {
        String ip = key.getCreateTCP(deviceId).getIp();
        List<String> names = (List<String>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(ip, "0xA1F0", deviceId));
        if (names != null && !names.isEmpty()) return names;

        List<String> groupNames = new ArrayList<>();
        for (int i = 0; i < NAMES_ADDR_ARR.length; i++) {
            if (i < 10) groupNames.add("分组0" + (i + 1));
            else groupNames.add("分组" + (i + 1));
        }
        return groupNames;
    }

    public List<String> getGroupNamesByIp(Integer deviceId, String ip) {
        List<String> names = (List<String>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(ip, "0xA1F0", deviceId));
        if (names != null && !names.isEmpty()) return names;

        List<String> groupNames = new ArrayList<>();
        for (int i = 0; i < NAMES_ADDR_ARR.length; i++) {
            if (i < 10) groupNames.add("分组0" + (i + 1));
            else groupNames.add("分组" + (i + 1));
        }
        return groupNames;
    }
}
