package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "模型使用场景")
public enum SceneCode {

    @Schema(description = "场景生成")
    SCENE_GEN(1, "SCENE_GEN", "场景生成"),

    @Schema(description = "信息提取")
    INFO_EXTRACT(2, "INFO_EXTRACT", "信息提取"),

    @Schema(description = "角色生成")
    CHARACTER_GEN(3, "CHARACTER_GEN", "角色生成"),

    @Schema(description = "分镜生成")
    STORYBOARD_GEN(4, "STORYBOARD_GEN", "分镜生成"),


    @Schema(description = "视频生成")
    VIDEO_GEN(5, "VIDEO_GEN", "视频生成"),

    @Schema(description = "音频生成")
    AUDIO_GEN(6, "AUDIO_GEN", "音频生成"),

    @Schema(description = "首帧生成")
    FIRST_FRAME_GEN(7, "FIRST_FRAME_GEN", "首帧生成");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "描述")
    private final String desc;

    public static SceneCode fromCode(Integer code) {
        for (SceneCode sc : values()) {
            if (sc.code.equals(code)) {
                return sc;
            }
        }
        return null;
    }

    public static SceneCode fromKey(String key) {
        for (SceneCode sc : values()) {
            if (sc.key.equalsIgnoreCase(key)) {
                return sc;
            }
        }
        return null;
    }
}
