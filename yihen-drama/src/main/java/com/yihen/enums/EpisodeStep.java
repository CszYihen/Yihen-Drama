package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
@Schema(description = "章节工作流步骤")
public enum EpisodeStep {

    @Schema(description = "输入内容")
    INPUT_CONTENT(0, "input_content", "输入内容"),

    @Schema(description = "提取信息")
    EXTRACT_INFO(1, "extract_info", "提取信息"),

    @Schema(description = "生成图片")
    GENERATE_IMAGES(2, "generate_images", "生成图片"),

    @Schema(description = "角色固定")
    CHARACTER_FIX(3, "character_fix", "固定角色"),

    @Schema(description = "生成分镜")
    GENERATE_STORYBOARD(4, "generate_storyboard", "生成分镜"),

    @Schema(description = "生成视频")
    GENERATE_VIDEO(5, "generate_video", "生成视频"),

    @Schema(description = "合成完成")
    COMPLETED(6, "completed", "合成完成");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static EpisodeStep fromKey(String key) {
        for (EpisodeStep value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown EpisodeStep key: " + key);
    }

    public static EpisodeStep fromCode(Integer code) {
        for (EpisodeStep value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown EpisodeStep code: " + code);
    }
}
