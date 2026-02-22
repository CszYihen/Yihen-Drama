package com.yihen.listener.model;


import com.yihen.constant.model.ModelMQConstant;
import com.yihen.constant.model.ModelManageRedisConstant;
import com.yihen.constant.project.ProjectMQConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.entity.Project;
import com.yihen.enums.ModelType;
import com.yihen.init.ModelCacheInitializer;
import com.yihen.search.mapper.ProjectDocMapper;
import com.yihen.search.repository.ProjectSearchRepository;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ModelListener {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ModelCacheInitializer modelCacheInitializer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_INSTANCE_ADD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_INSTANCE_ADD_KEY
            )
    )
    public void addModelInstance(ModelInstance modelInstance) {
        // TODO ES同步

        // Redis同步
        redisUtils.lPush(ModelManageRedisConstant.MODEL_INSTANCES_KEY+modelInstance.getModelType(),modelInstance);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_INSTANCE_DELETE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_INSTANCE_DELETE_KEY
            )
    )
    public void deleteModelInstance(ModelInstance modelInstance) {
        // TODO ES同步

        // Redis同步
        modelCacheInitializer.runByType(modelInstance.getModelType());
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_INSTANCE_UPDATE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_INSTANCE_UPDATE_KEY
            )
    )
    public void updateModelInstance(ModelInstance modelInstance) {
        // TODO ES同步

        // Redis同步
        modelCacheInitializer.runByType(modelInstance.getModelType());

    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_INSTANCE_LOAD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_INSTANCE_LOAD_KEY
            )
    )
    public void loadModelInstanceRedis(ModelType modelType) {
        // TODO ES同步

        // Redis同步
        modelCacheInitializer.runByType(modelType);
    }



    /* Model Definition */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_DEFINITION_ADD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_DEFINITION_ADD_KEY
            )
    )
    public void addModelDefinition(ModelDefinition modelDefinition) {
        // TODO ES同步

        // Redis同步
        redisUtils.lPush(ModelManageRedisConstant.MODEL_DEFINITIONS_KEY,modelDefinition);
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_DEFINITION_UPDATE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_DEFINITION_UPDATE_KEY
            )
    )
    public void updateModelDefinition(ModelDefinition modelDefinition) {
        // TODO ES同步

        // Redis同步
        modelCacheInitializer.runModelDefinition();

    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_DEFINITION_DELETE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_DEFINITION_DELETE_KEY
            )
    )
    public void deleteModelDefinition(Long id) {
        // TODO ES同步

        // Redis同步
        modelCacheInitializer.runModelDefinition();
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = ModelMQConstant.MODEL_DEFINITION_LOAD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = ModelMQConstant.MODEL_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = ModelMQConstant.MODEL_DEFINITION_LOAD_KEY
            )
    )
    public void loadModelDefinitionRedis() {

        // Redis同步
        modelCacheInitializer.runModelDefinition();
    }


}
