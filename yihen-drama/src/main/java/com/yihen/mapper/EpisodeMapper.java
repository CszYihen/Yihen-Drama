package com.yihen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.Episode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EpisodeMapper extends BaseMapper<Episode> {

    // 根据章节Id获取项目id
    @Select("SELECT project_id FROM episode WHERE id = #{episodeId}")
    Long getProjectIdByEpisodeId(@Param("episodeId") Long episodeId);

    // 根据章节Id获取章节内容
    @Select("SELECT content FROM episode WHERE id = #{episodeId}")
    String getContentByEpisodeId(@Param("episodeId") Long episodeId);

    // 根据项目Id获取所有关联的章节Id
    @Select("SELECT id FROM episode WHERE project_id = #{projectId}")
    List<Long> getEpisodeIdByProjectId(@Param("projectId") Long projectId);

    // 根据项目Id获取章节列表（不包含content）
    List<Episode> getEpisodesByProjectId(@Param("projectId") Long projectId);

}
