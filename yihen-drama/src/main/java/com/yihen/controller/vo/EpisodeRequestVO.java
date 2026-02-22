package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Episode 请求VO
 */
@Data
@Schema(description = "章节请求")
public class EpisodeRequestVO {

    @Schema(description = "章节ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "章节序号")
    private Integer chapterNumber;

    @Schema(description = "章节名称")
    private String name;

    @Schema(description = "章节内容")
    private String content;

    @Schema(description = "当前步骤")
    private Integer currentStep;

    @Schema(description = "进度")
    private Integer progress;
}
