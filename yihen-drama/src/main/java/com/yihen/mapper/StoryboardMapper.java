package com.yihen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.Storyboard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoryboardMapper extends BaseMapper<Storyboard> {
    // 根据EpisodeId获取对应的分镜Id

    @Select("select id from storyboard where episode_id = #{episodeId}")
    List<Long> getShotIdByEpisodeId(@Param("episodeId") Long episodeId);


}
