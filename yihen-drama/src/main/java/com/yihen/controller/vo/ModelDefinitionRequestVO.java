package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ModelDefinition 请求VO
 */
@Data
@Schema(description = "模型定义请求")
public class ModelDefinitionRequestVO {

    @Schema(description = "ID（更新时必传）")
    private Long id;


    @Schema(description = "厂商标识", example = "openai")
    private String providerCode;

    @Schema(description = "基础URL", example = "https://api.openai.com/v1")
    private String baseUrl;

    @Schema(description = "状态", example = "1-启用，0-禁用")
    private Byte status;
}
