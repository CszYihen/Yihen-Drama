package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "镜头类型")
public enum ShotType {

    @Schema(description = "远景")
    WIDE(1, "wide", "远景"),

    @Schema(description = "中景")
    MEDIUM(2, "medium", "中景"),

    @Schema(description = "近景")
    CLOSE_UP(3, "close-up", "近景"),

    @Schema(description = "特写")
    EXTREME_CLOSE_UP(4, "extreme-close-up", "特写");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static ShotType fromKey(String key) {
        for (ShotType value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ShotType key: " + key);
    }
}
