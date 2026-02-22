package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scene")
@Schema(description = "场景")
public class Scene extends BaseEntity {

    @Schema(description = "所属章节ID")
    private Long episodeId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "场景名称", example = "公司大堂")
    private String name;

    @Schema(description = "场景描述")
    private String description;

    @Schema(description = "场景缩略图URL")
    private String thumbnail;

    @TableField(exist = false)
    @JsonIgnore // 不会在 API 响应中暴露给前端或外部系统
    private boolean isNew;
}
