package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Id类型")
public enum IdType {

    @Schema(description = "Id")
    ID(0, "ID", "id"),

    @Schema(description = "章节Id")
    EPISODE_ID(1, "EPISODE_ID", "章节Id"),

    @Schema(description = "项目Id")
    PROJECT_ID(2, "PROJECT_ID", "项目Id");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static IdType fromKey(String key) {
        for (IdType value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStatus key: " + key);
    }

    public static IdType fromCode(Integer code) {
        for (IdType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStatus code: " + code);
    }
}
