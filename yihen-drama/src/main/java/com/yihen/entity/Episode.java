package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yihen.enums.EpisodeStatus;
import com.yihen.enums.EpisodeStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("episode")
@Schema(description = "章节")
public class Episode extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "章节序号")
    private Integer chapterNumber;

    @Schema(description = "章节名称", example = "第一章：意外相遇")
    private String name;

    @Schema(description = "原始小说内容")
    private String content;

    @Schema(description = "状态: 0-待开始 1-处理中 2-已完成")
    private EpisodeStatus status;

    @Schema(description = "处理进度百分比")
    private Integer progress;

    @Schema(description = "当前步骤: 0-输入内容 1-提取信息 2-生成图片 4-固定角色 5-生成分镜 6-生成视频 7-合成完成")
    private EpisodeStep currentStep;
}
