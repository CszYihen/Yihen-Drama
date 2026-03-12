package com.yihen.dto;


import com.yihen.enums.EpisodeStatus;
import com.yihen.enums.EpisodeStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小说章节内容切片元数据")
public class NovelChunk {

    @Schema(description = "所属章节ID")
    private Long episodeId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "章节序号")
    private Integer chapterNumber;

    @Schema(description = "章节名称", example = "第一章：意外相遇")
    private String name;

}
