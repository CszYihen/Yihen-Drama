package com.yihen.controller.vo;

import com.yihen.enums.ModelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "默认模型实例请求参数")
public class ModelInstanceDefaultRequestVO {
    @Schema(description = "模型类型 TEXT/IMAGE")
    private ModelType modelType;

    @Schema(description = "模型实例ID")
    private Long modelInstanceId;

}
