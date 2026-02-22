package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分镜生成参数")
public class StoryboardRequestVO {
    @Schema(description = "章节Id")
    private Long episodeId;

    @Schema(description = "项目Id")
    private Long projectId;

    @Schema(description = "模型id")
    private Long modelId;
}
