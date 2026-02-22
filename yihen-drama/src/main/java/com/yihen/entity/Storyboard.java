package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("storyboard")
@Schema(description = "分镜")
public class Storyboard extends BaseEntity {

    @Schema(description = "所属章节ID")
    private Long episodeId;

    @Schema(description = "镜头序号")
    private Integer shotNumber;

    @Schema(description = "镜头类型: 远景/中景/近景/特写")
    private String shotType;

    @Schema(description = "镜头时长(秒)")
    private Integer duration;

    @Schema(description = "画面描述")
    private String description;

    @Schema(description = "台词")
    private String dialogue;

    @Schema(description = "分镜缩略图URL")
    private String thumbnail;

    @Schema(description = "分镜视频URL")
    private String videoUrl;

    @Schema(description = "图片生成提示词")
    private String imagePrompt;

    @Schema(description = "视频生成提示词")
    private String videoPrompt;

    @Schema(description = "排序索引")
    private Integer orderIndex;


    @Schema(description = "关联的角色")
    @TableField(exist = false)
    private List<Characters> characters;

    @Schema(description = "关联的场景")
    @TableField(exist = false)
    private List<Scene> scenes;
}
