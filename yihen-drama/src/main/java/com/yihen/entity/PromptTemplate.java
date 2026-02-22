package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yihen.enums.SceneCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@TableName("prompt_template")
@Schema(description = "提示词模板")
public class PromptTemplate extends BaseEntity {

    @Schema(
        description = "提示词唯一编码",
        example = "INFO_EXTRACT_V1"
    )
    private String promptCode;



    @Schema(
        description = "提示词名称",
        example = "信息提取-人物场景物品"
    )
    private String promptName;

    @Schema(
        description = "业务场景编码（对应 SceneCode 枚举值）",
        example = "2"
    )
    private SceneCode sceneCode;

    @Schema(
        description = "提示词内容，支持 {{变量}} 占位符",
        example = "你是一名信息抽取助手...\n文本内容：{{input}}"
    )
    private String promptContent;
}
