package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色修改参数")
public class CharactersUpdateRequestVO {

    @Schema(description = "角色Id")
    private Long Id;

    @Schema(description = "角色名称", example = "宋伊裳")
    private String name;

    @Schema(description = "角色描述", example = "女，25岁，职场新人")
    private String description;
}
