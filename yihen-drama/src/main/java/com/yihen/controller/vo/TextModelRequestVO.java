package com.yihen.controller.vo;

import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文本模型请求数据")
public class TextModelRequestVO {
    @Schema(description = "章节Id")
    private Long episodeId;

    @Schema(description = "项目Id")
    private Long projectId;

    @Schema(description = "模型实例ID")
    private Long modelId;

    @Schema(description = "对话文本")
    private String text;

    @Schema(description = "场景")
    private SceneCode sceneCode;

    @Schema(description = "对象")
    private Object object;

}
