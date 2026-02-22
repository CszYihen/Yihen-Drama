package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("storyboard_scene")
@Schema(description = "分镜-场景关联表")
public class StoryBoardScene extends BaseEntity {

    @Schema(description = "分镜Id")
    private Long storyboardId;

    @Schema(description = "场景Id")
    private Long sceneId;

    @Schema(description = "章节Id")
    private Long episodeId;

    @Schema(description = "项目Id")
    private Long projectId;
}
