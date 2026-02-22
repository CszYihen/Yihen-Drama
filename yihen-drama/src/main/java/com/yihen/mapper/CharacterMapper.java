package com.yihen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.Characters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CharacterMapper extends BaseMapper<Characters> {

    // 根据章节id查询所有的角色id
    @Select("select id from characters where episode_id = #{episodeId}")
    List<Long> getCharacterIdsByEpisodeId(@Param("episodeId") Long episodeId);
}
