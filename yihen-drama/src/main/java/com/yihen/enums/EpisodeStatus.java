package com.yihen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "章节状态")
public enum EpisodeStatus {

    @Schema(description = "待开始")
    PENDING(0, "pending", "待开始"),

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

    public static EpisodeStatus fromKey(String key) {
        for (EpisodeStatus value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown EpisodeStatus key: " + key);
    }

    public static EpisodeStatus fromCode(Integer code) {
        for (EpisodeStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown EpisodeStatus code: " + code);
    }
}
