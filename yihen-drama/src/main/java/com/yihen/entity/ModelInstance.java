package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yihen.enums.ModelType;
import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "model_instance",autoResultMap = true)
@Schema(description = "模型实例")
public class ModelInstance extends BaseEntity {

    @Schema(description = "模型定义ID")
    private Long modelDefId;


    @Schema(description = "模型编码", example = "gpt-3.5-turbo/qwen-max")
    private String modelCode;
    @Schema(description = "模型类型", example = "TEXT/IMAGE/VIDEO")
    private ModelType modelType;

    @Schema(description = "模型实例名称",example = "剧情生成/分镜生成/通用")
    private String instanceName;

    @Schema(description = "模型使用场景",example = "SCENE_GEN / CHARACTER_GEN")
    private SceneCode sceneCode;

    @Schema(description = "apiKey")
    private String apiKey;

    @Schema(description = "模型参数" ,example = "{'  \"temperature\": 0.3,\n" +
            "  \"maxTokens\": 8192'}")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;


    @Schema(description = "模型状态",example = "1-启用，0-禁用")
    private Byte status;


    @Schema(description = "模型路径", example = "/compatible-mode/v1")
    private String path;


    @Schema(description = "厂商标识", example = "openai/aliyun/deepseek")
    @TableField(exist = false)
    private String providerCode;

    @Schema(description = "基础url", example = "https://api.deepseek.com")
    @TableField(exist = false)
    private String baseUrl;

    @Schema(description = "是否为默认实例")
    @TableField(exist = false)
    private Boolean isDefault;


}
