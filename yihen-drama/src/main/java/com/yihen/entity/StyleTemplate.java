package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.yihen.enums.ProjectStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "style_template", autoResultMap = true)
@Schema(description = "风格模板")
public class StyleTemplate extends BaseEntity {

    @Schema(description = "风格名称", example = "写实风格")
    private String name;


    @Schema(description = "风格描述")
    private String description;

    @Schema(description = "预览图URL")
    private String preview;

    @Schema(description = "风格参数配置")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> parameters;

    @Schema(description = "是否默认风格")
    private Byte isDefault;
}
