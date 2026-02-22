package com.yihen.controller.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "修改首帧提示词请求参数")
public class UpdateFirstFramePromptRequestVO {
    @Schema(description = "分镜id")
    private Long shotId;

    @Schema(description = "项目id")
    private Long projectId;

    @Schema(description = "提示词")
    private String text;
}
