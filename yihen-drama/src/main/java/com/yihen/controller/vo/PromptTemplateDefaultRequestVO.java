package com.yihen.controller.vo;

import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "默认提示词模板请求参数")
public class PromptTemplateDefaultRequestVO {

    @Schema(description = "场景代码")
    private SceneCode sceneCode;

    @Schema(description = "提示词模板ID")
    private Long promptTemplateId;

}
