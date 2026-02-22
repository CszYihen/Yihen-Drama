package com.yihen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.ModelDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ModelDefinitionMapper extends BaseMapper<ModelDefinition> {
    @Select("select base_url from model_definition where id = #{id}")
    String getBaseUrlById(@Param("id") Long id);
}
