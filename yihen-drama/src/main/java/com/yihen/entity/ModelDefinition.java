package com.yihen.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("model_definition")
@Schema(description = "厂商定义信息")
public class ModelDefinition extends BaseEntity{

    @Schema(description = "厂商标识", example = "openai/aliyun/deepseek")
    private String providerCode;


    @Schema(description = "基础url", example = "https://api.deepseek.com")
    private String baseUrl;

    @Schema(description = "模型状态", example = "1-启用，0-禁用")
    private Byte status;

}
