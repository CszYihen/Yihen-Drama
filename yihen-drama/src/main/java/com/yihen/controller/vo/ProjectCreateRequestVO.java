package com.yihen.controller.vo;

import com.yihen.enums.ProjectStatus;
import com.yihen.enums.ProjectStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "项目创建请求参数")
public class ProjectCreateRequestVO {

    @Schema(description = "项目名称", example = "霸道总裁爱上我")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "风格: 1-动漫 3-油画 4-赛博朋克")
    private Long style;
}
