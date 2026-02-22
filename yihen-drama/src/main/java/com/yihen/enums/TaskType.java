package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "任务类型")
public enum TaskType {

    @Schema(description = "角色视频生成")
    CHARACTER_VIDEO_GENERATION(1, "character_video_generation", "视频生成"),

    @Schema(description = "分镜视频生成")
    SHOT_VIDEO_GENERATION(2, "shot_video_generation", "视频生成"),



    @Schema(description = "音视频合成")
    COMPOSITING(3, "compositing", "音视频合成");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static TaskType fromKey(String key) {
        for (TaskType value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown TaskType key: " + key);
    }

    public static TaskType fromCode(Integer code) {
        for (TaskType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStyle code: " + code);
    }
}
