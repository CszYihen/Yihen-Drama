package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "场景添加参数")
public class SceneAddRequestVO {

    @Schema(description = "所属章节Id")
    private Long episodeId;

    @Schema(description = "场景名称", example = "教室")
    private String name;

    @Schema(description = "场景描述", example = "40张桌子，白色墙壁")
    private String description;
}
