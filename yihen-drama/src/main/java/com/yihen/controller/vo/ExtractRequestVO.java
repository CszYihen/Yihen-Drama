package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "信息提取请求参数")
public class ExtractRequestVO {

    @Schema(description = "章节ID")
    private Long episodeId;

    @Schema(description = "模型实例ID")
    private Long modelId;

}
