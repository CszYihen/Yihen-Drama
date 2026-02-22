package com.yihen.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.common.Result;
import com.yihen.controller.vo.PromptTemplateDefaultRequestVO;
import com.yihen.controller.vo.PromptTemplateRequestVO;
import com.yihen.entity.ModelInstanceDefault;
import com.yihen.entity.PromptTemplate;
import com.yihen.entity.PromptTemplateDefault;
import com.yihen.entity.Storyboard;
import com.yihen.enums.ModelType;
import com.yihen.enums.SceneCode;
import com.yihen.mapper.PromptTemplateDefaultMapper;
import com.yihen.service.PromptTemplateDefaultService;
import com.yihen.service.PromptTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prompts")
@Tag(name = "提示词管理接口", description = "管理提示词模板的增删改查")
public class PromptController {

    @Resource
    private PromptTemplateService promptTemplateService;

    @Resource
    private PromptTemplateDefaultService promptTemplateDefaultService;

    @GetMapping("/list")
    @Operation(summary = "获取提示词列表", description = "分页获取提示词模板列表")
    public Result<Page<PromptTemplate>> list(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long pageNum,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Long pageSize) {
        Page<PromptTemplate> page = new Page<>(pageNum, pageSize);
        Page<PromptTemplate> result = promptTemplateService.page(page);
        return Result.success(result);
    }

    @GetMapping("/list/all")
    @Operation(summary = "获取所有提示词", description = "返回所有提示词模板")
    public Result<List<PromptTemplate>> listAll() {
        List<PromptTemplate> list = promptTemplateService.list();
        return Result.success(list);
    }

    @GetMapping("/default/{sceneCode}")
    @Operation(summary = "获取场景默认提示词", description = "根据场景代码获取默认提示词模板")
    public Result<PromptTemplate> getDefaultByScene(@PathVariable SceneCode sceneCode) {
        PromptTemplate template = promptTemplateService.getDefaultTemplateBySceneCode(sceneCode);
        return Result.success(template);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取提示词详情")
    public Result<PromptTemplate> get(@PathVariable Long id) {
        PromptTemplate template = promptTemplateService.getById(id);
        return Result.success(template);
    }

    @PostMapping("/create")
    @Operation(summary = "创建提示词", description = "创建新的提示词模板")
    public Result<Void> create(@RequestBody PromptTemplateRequestVO requestVO) {
        PromptTemplate template = new PromptTemplate();
        BeanUtils.copyProperties(requestVO, template);
        promptTemplateService.save(template);
        return Result.success("创建成功");
    }

    @PutMapping("/update")
    @Operation(summary = "更新提示词", description = "更新提示词模板")
    public Result<Void> update(@RequestBody PromptTemplateRequestVO requestVO) {
        if (requestVO.getId() == null) {
            return Result.error("ID不能为空");
        }
        PromptTemplate template = new PromptTemplate();
        BeanUtils.copyProperties(requestVO, template);
        promptTemplateService.updateTemplate(template);
        return Result.success("更新成功");
    }

    @PutMapping("/update-default-prompt-template")
    @Operation(summary = "更新默认提示词模板", description = "接受提示词模板对象，更新对应的默认提示词")
    public Result<Void> updateDefaultPromptTemplate(@RequestBody PromptTemplateDefaultRequestVO requestVO) {


        PromptTemplateDefault promptTemplateDefault = new PromptTemplateDefault();
        BeanUtils.copyProperties(requestVO, promptTemplateDefault);

        promptTemplateDefaultService.updateDefault(promptTemplateDefault);

        return Result.success("更新成功");
    }


    @GetMapping("/default-prompt-template")
    @Operation(summary = "获取默认提示词模板", description = "返回各个场景下的提示词模板")
    public Result<Map<SceneCode, PromptTemplateDefault>> getDefaultModelInstance() {
        Map<SceneCode, PromptTemplateDefault> allDefaultUnderType = promptTemplateDefaultService.getAllDefaultUnderScene();
        return Result.success(allDefaultUnderType);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除提示词", description = "删除提示词模板")
    public Result<Void> delete(@PathVariable Long id) {
        promptTemplateService.deletePromptTemplateDefault(id);
        return Result.success("删除成功");
    }



}
