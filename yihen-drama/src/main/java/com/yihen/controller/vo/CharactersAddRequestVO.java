package com.yihen.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色添加参数")
public class CharactersAddRequestVO {

    @Schema(description = "所属章节Id")
    private Long episodeId;

    @Schema(description = "角色名称", example = "宋伊裳")
    private String name;

    @Schema(description = "角色描述", example = "女，25岁，职场新人")
    private String description;
}
