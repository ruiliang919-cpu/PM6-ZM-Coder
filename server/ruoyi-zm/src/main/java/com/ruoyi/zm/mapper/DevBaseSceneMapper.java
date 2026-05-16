package com.ruoyi.zm.mapper;

import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.zm.domain.DevBaseScene;
import com.ruoyi.zm.domain.vo.DevBaseSceneVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 场景Mapper接口
 *
 * @author mophi
 * @date 2024-07-11
 */
public interface DevBaseSceneMapper extends BaseMapperPlus<DevBaseSceneMapper, DevBaseScene, DevBaseSceneVo> {

    @Select("SELECT DISTINCT id,scene_id,name FROM dev_base_scene")
    List<DevBaseSceneVo> selectAllDistinctSceneId();

    @Delete("DELETE FROM dev_base_scene")
    void delete();
}
