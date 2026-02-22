package com.yihen.controller.vo;

import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "信息提取结果VO")
public class ExtractionResultVO {

    @Schema(description = "角色列表")
    private List<Characters> characters = new ArrayList<>();

    @Schema(description = "场景列表")
    private List<Scene> scenes = new ArrayList<>();

}
