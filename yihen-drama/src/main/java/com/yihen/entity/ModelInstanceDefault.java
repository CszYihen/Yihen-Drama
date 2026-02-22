package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yihen.enums.ModelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_instance_default ")
@Schema(description = "各类型下默认的模型实例表")
public class ModelInstanceDefault extends BaseEntity {

    @Schema(description = "模型类型 TEXT/IMAGE")
    private ModelType modelType;

    @Schema(description = "模型实例ID")
    private Long modelInstanceId;

    @Schema(description = "状态", example = "1-启用，0-禁用")
    private Byte status;

    @Schema(description = "备注说明")
    private String remark;


}
