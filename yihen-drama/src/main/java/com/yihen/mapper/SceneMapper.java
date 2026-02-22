package com.yihen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.Scene;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SceneMapper extends BaseMapper<Scene> {
    // 根据章节id查询所有的角色id
    @Select("select id from scene where episode_id = #{episodeId}")
    List<Long> getSceneIdsByEpisodeId(@Param("episodeId") Long episodeId);
}
