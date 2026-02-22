package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yihen.enums.ModelType;
import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prompt_template_default ")
@Schema(description = "各业务场景下默认提示词模板表")
public class PromptTemplateDefault extends BaseEntity {

    @Schema(description = "业务场景")
    private SceneCode sceneCode;

    @Schema(description = "提示词模板ID")
    private Long promptTemplateId;

    @Schema(description = "状态", example = "1-启用，0-禁用")
    private byte status;

    @Schema(description = "备注说明")
    private String remark;


}
