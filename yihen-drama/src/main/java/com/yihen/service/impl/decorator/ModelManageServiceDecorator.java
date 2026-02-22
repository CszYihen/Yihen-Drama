package com.yihen.service.impl.decorator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.constant.model.ModelMQConstant;
import com.yihen.constant.model.ModelManageRedisConstant;
import com.yihen.constant.project.ProjectMQConstant;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.enums.ModelType;
import com.yihen.init.ModelCacheInitializer;
import com.yihen.mapper.ModelDefinitionMapper;
import com.yihen.service.ModelManageService;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Primary // 让业务默认注入到“带缓存的实现”
@Slf4j
public class ModelManageServiceDecorator extends ServiceImpl<ModelDefinitionMapper, ModelDefinition> implements ModelManageService {

    private final ModelManageService modelManageService;            // 被装饰者
    private final RedisUtils redisUtils;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private ModelCacheInitializer modelCacheInitializer;

    public ModelManageServiceDecorator(
            @Qualifier("modelManageServiceImpl") ModelManageService modelManageService, RedisUtils redisUtils, RabbitTemplate rabbitTemplate) {
        this.modelManageService = modelManageService;
        this.redisUtils = redisUtils;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void addModelDefinition(ModelDefinition modelDefinition) {
        modelManageService.addModelDefinition(modelDefinition);

        // 发送MQ消息,添加对应缓存
        rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_DEFINITION_ADD_KEY, modelDefinition);

        // 添加对应缓存
//        redisUtils.lPush(ModelManageRedisConstant.MODEL_DEFINITIONS_KEY,modelDefinition);
    }

    @Override
    public void addModelInstance(ModelInstance modelInstance) {
        modelManageService.addModelInstance(modelInstance);
        // 发送MQ消息,添加对应缓存
        rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_INSTANCE_ADD_KEY, modelInstance);
//        redisUtils.lPush(ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelInstance.getModelType(),modelInstance);
    }

    @Override
    public List<ModelInstance> getModelInstanceByType(Page<ModelInstance> modelInstancePage, ModelType modelType) {
        // 1. 查询缓存，是否存在对应数据
        // 1.1 构建分页参数
        long page = modelInstancePage.getCurrent();
        long size = modelInstancePage.getSize();
        long start = (page - 1) * size;
        long end = start + size - 1;

        List<ModelInstance> modelInstanceByType =redisUtils.lRange(ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelType, start, end, ModelInstance.class);
        Long total = redisUtils.lSize(ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelType);
        modelInstancePage.setRecords(modelInstanceByType);
        modelInstancePage.setTotal(total);


        if (ObjectUtils.isEmpty(modelInstanceByType)) {
            // 2. 缓存为空，查询数据库
            modelInstanceByType = modelManageService.getModelInstanceByType(modelInstancePage, modelType);
            // 发送MQ消息，同步Redis
            rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_INSTANCE_LOAD_QUEUE, modelType);
//            modelCacheInitializer.runByType(modelType);
        }
        return modelInstanceByType;
    }

    @Override
    public void testModelInstanceConnectivity(Long id) {
        modelManageService.testModelInstanceConnectivity(id);
    }

    @Override
    public void deleteModelInstance(Long id) {
        // 删除对应缓存
        ModelInstance modelInstance = modelManageService.getModelInstanceById(id);

        // 发送MQ消息，同步Redis 和 ES
        rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_INSTANCE_DELETE_KEY, modelInstance);


//        modelCacheInitializer.runByType(modelInstance.getModelType());
//        redisUtils.delete(ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelInstance.getModelType());

        modelManageService.deleteModelInstance(id);

    }

    @Override
    public void deleteModelDefinition(Long id) {
        modelManageService.deleteModelDefinition(id);
        // 发送MQ消息,添加对应缓存
        rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_DEFINITION_DELETE_KEY, id);
//        modelCacheInitializer.runModelDefinition();
    }
    @Override
    public void updateModelInstance(ModelInstance modelInstance) {
        modelManageService.updateModelInstance(modelInstance);

        // 发送MQ消息,添加对应缓存
        rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_INSTANCE_UPDATE_KEY, modelInstance);

    }

    @Override
    public ModelInstance getDefaultModelInstanceByType(ModelType modelType) {


        return modelManageService.getDefaultModelInstanceByType(modelType);
    }

    @Override
    public void updateModelDefinition(ModelDefinition modelDefinition) {
        modelManageService.updateModelDefinition(modelDefinition);

        // 发送MQ消息,添加对应缓存
        rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_DEFINITION_UPDATE_KEY, modelDefinition);

    }

    @Override
    public ModelInstance getModelInstanceById(Long id) {
        return modelManageService.getModelInstanceById(id);
    }

    @Override
    public String getBaseUrlById(Long id) {
        return modelManageService.getBaseUrlById(id);
    }

    @Override
    public List<ModelDefinition> getModelDefinition(Page<ModelDefinition> modelDefinitionPage) {

        // 1. 查询缓存，是否存在对应数据
        // 1.1 构建分页参数
        long page = modelDefinitionPage.getCurrent();
        long size = modelDefinitionPage.getSize();
        long start = (page - 1) * size;
        long end = start + size - 1;

        List<ModelDefinition> modelDefinitions =redisUtils.lRange(ModelManageRedisConstant.MODEL_DEFINITIONS_KEY, start, end, ModelDefinition.class);
        Long total = redisUtils.lSize(ModelManageRedisConstant.MODEL_DEFINITIONS_KEY);
        modelDefinitionPage.setRecords(modelDefinitions);
        modelDefinitionPage.setTotal(total);


        if (ObjectUtils.isEmpty(modelDefinitions)) {
            // 2. 缓存为空，查询数据库
            modelDefinitions = modelManageService.getModelDefinition(modelDefinitionPage);
            // 发送MQ消息,添加对应缓存
            rabbitTemplate.convertAndSend(ModelMQConstant.MODEL_EXCHANGE, ModelMQConstant.MODEL_DEFINITION_LOAD_KEY, 1L);

//            modelCacheInitializer.runModelDefinition();
        }
        return modelDefinitions;
    }

}
