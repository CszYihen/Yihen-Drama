package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "项目状态")
public enum ProjectStatus {

    @Schema(description = "草稿")
    DRAFT(0, "draft", "草稿"),

    @Schema(description = "处理中")
    PROCESSING(1, "processing", "处理中"),

    @Schema(description = "已完成")
    COMPLETED(2, "completed", "已完成");

    @EnumValue
    @Schema(description = "数据库存储值")
    private final Integer code;

    @Schema(description = "前端传递的标识")
    private final String key;

    @Schema(description = "中文描述")
    private final String desc;

    public static ProjectStatus fromKey(String key) {
        for (ProjectStatus value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStatus key: " + key);
    }

    public static ProjectStatus fromCode(Integer code) {
        for (ProjectStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ProjectStatus code: " + code);
    }
}
