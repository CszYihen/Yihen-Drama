package com.yihen.controller.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@Schema(description = "角色")
public class CharactersRequestVO  {
    @Schema(description = "模型实例id")
    private Long modelInstanceId;



    @Schema(description = "角色Id")
    private Long charcterId;

    @Schema(description = "项目Id", example = "1")
    private Long ProjectId;

    @Schema(description = "角色描述", example = "女，25岁，职场新人")
    private String description;
}
