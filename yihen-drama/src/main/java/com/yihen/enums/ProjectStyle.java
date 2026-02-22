package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "项目风格")
public enum ProjectStyle {

    @Schema(description = "写实风格")
    REALISTIC(1, "realistic", "写实风格", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"),

    @Schema(description = "动漫风格")
    ANIME(2, "anime", "动漫风格", "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"),

    @Schema(description = "油画质感")
    OIL(3, "oil", "油画质感", "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"),

    @Schema(description = "赛博朋克")
    CYBER(4, "cyber", "赛博朋克", "linear-gradient(135deg, #fa709a 0%, #fee140 100%)");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    @Schema(description = "预览渐变色")
    private final String preview;

    public static ProjectStyle fromKey(String key) {
        for (ProjectStyle value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStyle key: " + key);
    }

    public static ProjectStyle fromCode(Integer code) {
        for (ProjectStyle value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStyle code: " + code);
    }
}
