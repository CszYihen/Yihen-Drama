package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yihen.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("storyboard_character")
@Schema(description = "分镜-角色关联表")
public class StoryBoardCharacter extends BaseEntity {

    @Schema(description = "分镜Id")
    private Long storyboardId;

    @Schema(description = "角色Id")
    private Long characterId;

    @Schema(description = "章节Id")
    private Long episodeId;

    @Schema(description = "项目Id")
    private Long projectId;
}
