package com.yihen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("SELECT style_id FROM project WHERE id = #{id}")
    Long getProjectStyleById(Long id);

    @Update("""
        UPDATE project
        SET chapter_count = chapter_count - 1
        WHERE id = #{projectId}
    """)
    void reduceEpisodeCount(@Param("projectId") Long projectId);

    @Update("""
        UPDATE project
        SET chapter_count = chapter_count + 1
        WHERE id = #{projectId}
    """)
    void addEpisodeCount(@Param("projectId") Long projectId);


    ExtractionResultVO getPropertyById(@Param("projectId") Long projectId);

}
