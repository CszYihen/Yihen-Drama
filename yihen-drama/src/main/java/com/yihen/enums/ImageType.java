package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "图片类型")
public enum ImageType {

    @Schema(description = "角色图片")
    CHARACTER(1, "character", "角色图片"),

    @Schema(description = "场景图片")
    SCENE(2, "scene", "场景图片");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static ImageType fromKey(String key) {
        for (ImageType value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ImageType key: " + key);
    }
}
