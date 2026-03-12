package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "向量请求参数")
public class EmbeddingModelRequestVO {
    @Schema(description = "模型实例id")
    private Long modelInstanceId;

    @Schema(description = "项目Id", example = "1")
    private Long projectId;

    @Schema(description = "内容", example = "女，25岁，职场新人")
    private List<String> input;

    @Schema(description = "对象")
    private Object object;

}
