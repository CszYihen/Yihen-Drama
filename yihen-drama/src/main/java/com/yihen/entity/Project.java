package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yihen.enums.ProjectStyle;
import com.yihen.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("project")
@Schema(description = "项目")
public class Project extends BaseEntity {

    @Schema(description = "项目名称", example = "霸道总裁爱上我")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "封面URL")
    private String cover;

    @Schema(description = "风格: 2-动漫 1-写实 3-油画 4-赛博朋克")
    private Long styleId;

    @Schema(description = "状态: 0-草稿 1-处理中 2-已完成")
    private ProjectStatus status;

    @Schema(description = "一致性百分比")
    private Integer consistency;

    @Schema(description = "章节数量")
    private Integer chapterCount;

    @Schema(description = "总体进度百分比")
    private Integer progress;
}
