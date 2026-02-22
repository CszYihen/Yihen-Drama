package com.yihen.controller.vo;

import com.yihen.entity.ModelInstance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "模型实例响应数据")
public class ModelInstanceResponseVo {
    @Schema(description = "模型实例列表")
    private List<ModelInstance> modelInstances;
    @Schema(description = "模型实例个数")
    private Integer total;
}
