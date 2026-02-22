package com.yihen.controller.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "首帧请求参数")
public class FirstFrameRequestVO {
    @Schema(description = "分镜id")
    private Long shotId;

    @Schema(description = "项目id")
    private Long projectId;

    @Schema(description = "模型实例id")
    private Long modelInstanceId;

    @Schema(description = "模型参数" ,example = "{  \"temperature\": 0.3,\n" +
            "  \"duration\": 5 }")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;
}
