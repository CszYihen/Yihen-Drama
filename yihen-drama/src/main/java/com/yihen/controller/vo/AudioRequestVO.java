package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "音频请求参数")
public class AudioRequestVO {
    @Schema(description = "角色Id")
    private Long characterId;

    @Schema(description = "模型实例id")
    private Long modelInstanceId;

    @Schema(description = "指定音色")
    private String voice;

    @Schema(description = "生成语音文本", example = "大家好，我是xxx")
    private String text;


}
