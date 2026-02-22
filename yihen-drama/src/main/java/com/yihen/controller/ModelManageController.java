package com.yihen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihen.common.Result;
import com.yihen.controller.vo.ModelDefinitionRequestVO;
import com.yihen.controller.vo.ModelInstanceDefaultRequestVO;
import com.yihen.controller.vo.ModelInstanceRequestVO;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.entity.ModelInstanceDefault;
import com.yihen.enums.ModelType;
import com.yihen.service.ModelInstanceDefaultService;
import com.yihen.service.ModelManageService;
import com.yihen.controller.vo.ModelInstanceResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/models")
@Tag(name = "模型管理接口", description = "管理模型操作，包括模型实例的查询、新建等")
public class ModelManageController {
    @Resource
    private ModelManageService modelManageService;

    @Autowired
    private ModelInstanceDefaultService modelInstanceDefaultService;

    @GetMapping("/model-definition/list")
    @Operation(summary = "获取厂商列表", description = "返回所有厂商定义")
    public Result<Page<ModelDefinition>> listModelDefinitions(
            @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer page,  // 参数描述
            @Parameter(description = "每页显示数量", example = "10") @RequestParam(defaultValue = "6") Integer size) {


        Page<ModelDefinition> modelDefinitionPage = new Page<>(page, size);

        modelManageService.getModelDefinition(modelDefinitionPage);
        return Result.success(modelDefinitionPage);
    }

    @GetMapping("/model-definition/{id}")
    @Operation(summary = "获取厂商详情")
    public Result<ModelDefinition> getModelDefinition(@PathVariable Long id) {
        ModelDefinition definition = modelManageService.getById(id);
        return Result.success(definition);
    }

    @GetMapping("/model-instance-by-type/{modelType}")
    @Operation(summary = "获取指定类型的模型实例", description = "接受模型实例类型，返回对应的模型实例列表和数量")
    public Result<Page<ModelInstance>> getModelInstanceByType(
            @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer page,  // 参数描述
            @Parameter(description = "每页显示数量", example = "10") @RequestParam(defaultValue = "6") Integer size,
            @Parameter(description = "模型实例类型", example = "TEXT/IMAGE/VIDEO") @PathVariable ModelType modelType) {

        Page<ModelInstance> modelInstancePage = new Page<>(page, size);
        modelManageService.getModelInstanceByType(modelInstancePage,modelType);
        return Result.success(modelInstancePage);
    }

    @PostMapping("/add-model-definition")
    @Operation(summary = "添加厂商", description = "接受厂商信息，添加厂商")
    public Result<Void> addModelDefinition(
            @Parameter(description = "厂商信息对象") @RequestBody ModelDefinitionRequestVO modelDefinitionRequestVO) {

        ModelDefinition modelDefinition = new ModelDefinition();
        BeanUtils.copyProperties(modelDefinitionRequestVO, modelDefinition);


        modelManageService.addModelDefinition(modelDefinition);
        return Result.success("添加成功");
    }

    @PutMapping("/model-definition")
    @Operation(summary = "更新厂商", description = "接受厂商信息，更新厂商")
    public Result<Void> updateModelDefinition(
            @Parameter(description = "厂商信息对象") @RequestBody ModelDefinitionRequestVO modelDefinitionRequestVO) {

        ModelDefinition modelDefinition = new ModelDefinition();
        BeanUtils.copyProperties(modelDefinitionRequestVO, modelDefinition);

        modelManageService.updateModelDefinition(modelDefinition);
        return Result.success("更新成功");
    }

    @DeleteMapping("/model-definition/{id}")
    @Operation(summary = "删除厂商", description = "接受厂商ID，删除厂商")
    public Result<Void> deleteModelDefinition(
            @Parameter(description = "厂商ID", example = "1") @PathVariable Long id) {
        modelManageService.deleteModelDefinition( id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/model-instance/{id}")
    @Operation(summary = "删除模型实例", description = "接受模型实例ID，删除对应的模型实例")
    public Result<Void> deleteModelInstance(
            @Parameter(description = "模型实例ID", example = "1") @PathVariable Long id) {

        modelManageService.deleteModelInstance(id);
        return Result.success("删除成功");
    }

    @PostMapping("/update-model-instance")
    @Operation(summary = "更新模型实例", description = "接受模型实例对象，更新对应的模型实例")
    public Result<Void> updateModelInstance(
            @Parameter(description = "模型实例对象") @RequestBody ModelInstanceRequestVO modelInstanceRequestVO) {
        ModelInstance modelInstance = new ModelInstance();
        BeanUtils.copyProperties(modelInstanceRequestVO, modelInstance);
        modelManageService.updateModelInstance(modelInstance);
        return Result.success("更新成功");
    }

    @PostMapping("/add-model-instance")
    @Operation(summary = "添加模型实例", description = "接受模型实例对象，添加对应的模型实例")
    public Result<Void> addModelInstance(
            @Parameter(description = "模型实例对象") @RequestBody ModelInstanceRequestVO modelInstanceRequestVO) {

        ModelInstance modelInstance = new ModelInstance();
        BeanUtils.copyProperties(modelInstanceRequestVO, modelInstance);

        modelManageService.addModelInstance(modelInstance);
        return Result.success("添加成功");
    }

    @GetMapping("/default-model-instance")
    @Operation(summary = "获取默认模型实例", description = "返回各个默认模型实例")
    public Result<Map<ModelType, ModelInstanceDefault>> getDefaultModelInstance() {
        Map<ModelType, ModelInstanceDefault> allDefaultUnderType = modelInstanceDefaultService.getAllDefaultUnderType();
        return Result.success(allDefaultUnderType);
    }

    @GetMapping("/default-model-instance-by-type/{modelType}")
    @Operation(summary = "获取指定类型的默认模型实例")
    public Result<ModelInstance> getDefaultModelInstanceByType(@PathVariable ModelType modelType) {
        ModelInstance defaultInstance = modelManageService.getDefaultModelInstanceByType(modelType);
        return Result.success(defaultInstance);
    }

    @PostMapping("/add-default-model-instance")
    @Operation(summary = "添加默认模型实例", description = "接受模型实例对象，添加对应的模型实例")
    public Result<Void> addDefaultModelInstance(@RequestBody ModelInstanceDefaultRequestVO modelInstanceDefaultRequestVO) {

        ModelInstanceDefault modelInstanceDefault = new ModelInstanceDefault();
        BeanUtils.copyProperties(modelInstanceDefaultRequestVO, modelInstanceDefault);

        modelInstanceDefaultService.addDefault(modelInstanceDefault);
        return Result.success("添加成功");
    }

    @PutMapping("/update-default-model-instance")
    @Operation(summary = "更新默认模型实例", description = "接受模型实例对象，更新对应的模型实例")
    public Result<Void> updateDefaultModelInstance(@RequestBody ModelInstanceDefaultRequestVO modelInstanceDefaultRequestVO) {

        ModelInstanceDefault modelInstanceDefault = new ModelInstanceDefault();
        BeanUtils.copyProperties(modelInstanceDefaultRequestVO, modelInstanceDefault);

        modelInstanceDefaultService.updateDefault(modelInstanceDefault);
        return Result.success("更新成功");
    }



}
