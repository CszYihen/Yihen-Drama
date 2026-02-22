package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "场景修改参数")
public class SceneUpdateRequestVO {

    @Schema(description = "场景Id")
    private Long Id;

    @Schema(description = "场景名称", example = "食堂")
    private String name;

    @Schema(description = "场景描述", example = "大长桌子一排排")
    private String description;
}
