package com.yihen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.PromptTemplateDefault;
import com.yihen.enums.SceneCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PromptTemplateDefaultMapper extends BaseMapper<PromptTemplateDefault> {


    @Select("SELECT prompt_template_id FROM prompt_template_default WHERE scene_code = #{sceneCode}")
    Long getPromptTemplateDefaultIdBySceneCode(@Param("sceneCode") SceneCode sceneCode);
}
