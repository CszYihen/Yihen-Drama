package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "场景请求参数")
public class SceneRequestVO {
    @Schema(description = "模型实例id")
    private Long modelInstanceId;



    @Schema(description = "场景Id")
    private Long sceneId;

    @Schema(description = "项目Id", example = "1")
    private Long ProjectId;

    @Schema(description = "场景描述", example = "空旷的屋子")
    private String description;
}
