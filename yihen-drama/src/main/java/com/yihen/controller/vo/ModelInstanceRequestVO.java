package com.yihen.controller.vo;

import com.yihen.enums.ModelType;
import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * ModelInstance 请求VO
 */
@Data
@Schema(description = "模型实例请求")
public class ModelInstanceRequestVO {

    @Schema(description = "ID（更新时必传）")
    private Long id;

    @Schema(description = "模型定义ID")
    private Long modelDefId;

    @Schema(description = "模型编码", example = "gpt-4o")
    private String modelCode;

    @Schema(description = "模型类型", example = "TEXT")
    private ModelType modelType;

    @Schema(description = "实例名称", example = "GPT-4 默认")
    private String instanceName;

    @Schema(description = "使用场景", example = "STORY_PARSE")
    private SceneCode sceneCode;

    @Schema(description = "API Key")
    private String apiKey;

    @Schema(description = "模型参数")
    private Map<String, Object> params;

    @Schema(description = "模型路径", example = "/chat/completions")
    private String path;

    @Schema(description = "状态", example = "1-启用，0-禁用")
    private Byte status;

}
