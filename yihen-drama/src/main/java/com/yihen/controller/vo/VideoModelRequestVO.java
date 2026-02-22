package com.yihen.controller.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "视频请求参数")
public class VideoModelRequestVO {
    @Schema(description = "模型实例id")
    private Long modelInstanceId;

    @Schema(description = "项目Id", example = "1")
    private Long projectId;

    @Schema(description = "描述", example = "女，25岁，职场新人")
    private String description;

    @Schema(description = "对象")
    private Object object;

    @Schema(description = "模型参数" ,example = "{'  \"temperature\": 0.3,\n" +
            "  \"max_tokens\": 8192'}")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;

}
