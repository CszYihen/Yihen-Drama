package com.yihen.core.model.strategy.video.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频任务信息")
public class VideoTaskDTO {
    @Schema(description = "任务状态")
    String status;
    @Schema(description = "视频地址")
    String videoUrl;
    @Schema(description = "返回信息")
    String message;
}
