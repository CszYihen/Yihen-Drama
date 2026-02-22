package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "章节创建请求参数")
public class EpisodeCreateRequestVO {
    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "章节序号")
    private Integer chapterNumber;

    @Schema(description = "章节名称", example = "第一章：意外相遇")
    private String name;

    @Schema(description = "原始小说内容")
    private String content;

}
