package com.yihen.service.impl.decorator;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.constant.model.ModelInstanceDefaultRedisConstant;
import com.yihen.entity.ModelInstanceDefault;
import com.yihen.enums.ModelType;
import com.yihen.mapper.ModelInstanceDefaultMapper;
import com.yihen.service.ModelInstanceDefaultService;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Primary // 让业务默认注入到“带缓存的实现”
@Slf4j
public class ModelInstanceDefaultServiceDecorator extends ServiceImpl<ModelInstanceDefaultMapper,ModelInstanceDefault> implements ModelInstanceDefaultService {

    private final ModelInstanceDefaultService modelInstanceDefaultService;            // 被装饰者
    private final RedisUtils redisUtils;


    public ModelInstanceDefaultServiceDecorator(
            @Qualifier("modelInstanceDefaultServiceImpl") ModelInstanceDefaultService modelInstanceDefaultService, RedisUtils redisUtils) {
        this.modelInstanceDefaultService = modelInstanceDefaultService;
        this.redisUtils = redisUtils;
    }

    @Override
    public Map<ModelType, ModelInstanceDefault> getAllDefaultUnderType() {
        // 1. 查询缓存，是否存在对应数据
        Map<ModelType, ModelInstanceDefault> map =(Map<ModelType, ModelInstanceDefault>) redisUtils.get(ModelInstanceDefaultRedisConstant.MODEL_INSTANCE_DEFAULT_KEY);
        if (ObjectUtils.isEmpty(map)) {
            // 2. 缓存为空，查询数据库
            map = modelInstanceDefaultService.getAllDefaultUnderType();
            // 3. 添加缓存
            redisUtils.putHash(ModelInstanceDefaultRedisConstant.MODEL_INSTANCE_DEFAULT_KEY,map,1,TimeUnit.DAYS);
        }

        return map;
    }

    @Override
    public boolean checkExistUnderType(ModelType modelType) {

        return modelInstanceDefaultService.checkExistUnderType(modelType);
    }

    @Override
    public boolean checkIsDefault(Long modelInstanceId) {
        return modelInstanceDefaultService.checkIsDefault(modelInstanceId);
    }

    @Override
    public void addDefault(ModelInstanceDefault modelInstanceDefault) {
        modelInstanceDefaultService.addDefault(modelInstanceDefault);
        // 删除对应缓存
        redisUtils.delete(ModelInstanceDefaultRedisConstant.MODEL_INSTANCE_DEFAULT_KEY);

    }

    @Override
    public void deleteDefault(Long modelInstanceId, ModelType modelType) {
        modelInstanceDefaultService.deleteDefault(modelInstanceId, modelType);
        // 删除对应缓存
        redisUtils.delete(ModelInstanceDefaultRedisConstant.MODEL_INSTANCE_DEFAULT_KEY);
    }

    @Override
    public void updateDefault(ModelInstanceDefault modelInstanceDefault) {
        modelInstanceDefaultService.updateDefault(modelInstanceDefault);
        // 删除对应缓存
        redisUtils.delete(ModelInstanceDefaultRedisConstant.MODEL_INSTANCE_DEFAULT_KEY);
    }
}
