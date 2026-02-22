package com.yihen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.controller.vo.ModelInstanceResponseVo;
import com.yihen.enums.ModelType;

import java.util.List;

public interface ModelManageService extends IService<ModelDefinition> {

    /**
     * 添加厂商定义
     * @param modelDefinition
     */
    void addModelDefinition(ModelDefinition modelDefinition);

    /**
     * 添加模型实例
     * @param modelInstance
     */
    void addModelInstance(ModelInstance modelInstance);

    /**
     * 获取模型定义列表
     *
     * @return
     */
    List<ModelInstance> getModelInstanceByType(Page<ModelInstance> modelInstancePage,ModelType modelType);


    /**
     * 测试模型实例连接性
     * @param id 模型实例ID
     */
    void testModelInstanceConnectivity(Long id);

    /**
     * 删除模型实例
     * @param id 模型实例ID
     */
    void deleteModelInstance(Long id);

    /**
     * 删除模型定义
     * @param id 模型定义ID
     */
    void deleteModelDefinition(Long id);

    void updateModelInstance(ModelInstance modelInstance);

    /**
     * 获取指定类型的默认模型实例
     * @param modelType 模型类型
     * @return 默认模型实例，如果没有设置默认则返回null
     */
    ModelInstance getDefaultModelInstanceByType(ModelType modelType);

    /**
     * 更新厂商定义
     * @param modelDefinition
     */
    void updateModelDefinition(ModelDefinition modelDefinition);

    /**
     * 获取模型实例
     * @param id 模型实例ID
     * @return
     */
    ModelInstance getModelInstanceById(Long id);

    /**
     * 获取模型定义的baseUrl
     * @param id 模型定义ID
     * @return
     */
    String getBaseUrlById(Long id);

    List<ModelDefinition> getModelDefinition(Page<ModelDefinition> modelDefinitionPage);
}
