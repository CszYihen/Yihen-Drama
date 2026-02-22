package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "characters", autoResultMap = true)
@Schema(description = "角色")
public class Characters extends BaseEntity {

    @Schema(description = "所属章节ID")
    private Long episodeId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "角色名称", example = "林悦")
    private String name;

    @Schema(description = "角色描述", example = "女，25岁，职场新人")
    private String description;

    @Schema(description = "角色形象URL")
    private String avatar;

    @Schema(description = "角色视频URL")
    private String videoUrl;

    @Schema(description = "角色特征描述(用于一致性生成)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> features;

    @TableField(exist = false)
    private boolean isNew;
}
