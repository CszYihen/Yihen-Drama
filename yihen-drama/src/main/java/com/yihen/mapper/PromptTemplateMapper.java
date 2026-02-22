package com.yihen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.PromptTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PromptTemplateMapper extends BaseMapper<PromptTemplate> {
}
