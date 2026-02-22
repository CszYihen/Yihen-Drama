package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "模型类型")
public enum ModelType {

    @Schema(description = "文本模型")
    TEXT(1, "TEXT", "文本模型"),

    @Schema(description = "图像模型")
    IMAGE(2, "IMAGE", "图像模型"),

    @Schema(description = "视频模型")
    VIDEO(3, "VIDEO", "视频模型"),

    @Schema(description = "音频模型")
    AUDIO(4, "AUDIO", "音频模型");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static ModelType fromKey(String key) {
        for (ModelType value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ModelType key: " + key);
    }
}
