package com.yihen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yihen.enums.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_task")
@Schema(description = "视频任务")
public class VideoTask extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "所属实例ID")
    private Long instanceId;

    @Schema(description = "所属资产ID,角色id或分镜id")
    private Long targetId;


    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "任务类型")
    private TaskType taskType;

    @Schema(description = "任务状态" ,example = "success")
    private String status;

    @Schema(description = "进度百分比")
    private Integer progress;

    @Schema(description = "总体进度")
    private Integer overallProgress;

    @Schema(description = "生成的视频URL")
    private String videoUrl;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "下次允许查询的时间")
    private Date nextPollAt;

    @Schema(description = "轮询次数")
    private Integer pollCount;
}
