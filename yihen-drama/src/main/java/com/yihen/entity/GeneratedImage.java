package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("generated_image")
@Schema(description = "生成图片")
public class GeneratedImage extends BaseEntity {

    @Schema(description = "所属章节ID")
    private Long episodeId;

    @Schema(description = "图片类型: character-角色 scene-场景")
    private String imageType;

    @Schema(description = "关联角色或场景的ID")
    private Long targetId;

    @Schema(description = "生成的图片URL")
    private String imageUrl;

    @Schema(description = "生成使用的提示词")
    private String prompt;

    @Schema(description = "状态: 0-生成中 1-成功 2-失败")
    private Byte status;
}
