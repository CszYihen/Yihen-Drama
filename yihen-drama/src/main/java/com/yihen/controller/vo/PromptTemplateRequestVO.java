package com.yihen.controller.vo;

import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提示词模板请求VO
 */
@Data
@Schema(description = "提示词模板请求")
public class PromptTemplateRequestVO {

    @Schema(description = "ID（更新时必传）")
    private Long id;

    @Schema(description = "提示词唯一编码", example = "INFO_EXTRACT_V1")
    private String promptCode;

    @Schema(description = "提示词名称", example = "信息提取-人物场景物品")
    private String promptName;

    @Schema(description = "业务场景编码", example = "2")
    private SceneCode sceneCode;

    @Schema(description = "提示词内容，支持 {{变量}} 占位符", example = "你是一名信息抽取助手...\n文本内容：{{input}}")
    private String promptContent;

    @Schema(description = "是否为默认 0-非默认 1-默认", example = "0")
    private Byte isDefault;
}
