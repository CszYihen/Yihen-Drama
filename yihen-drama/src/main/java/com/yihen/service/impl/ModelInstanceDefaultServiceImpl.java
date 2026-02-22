package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.entity.ModelInstanceDefault;
import com.yihen.enums.ModelType;
import com.yihen.mapper.ModelInstanceDefaultMapper;
import com.yihen.service.ModelInstanceDefaultService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("modelInstanceDefaultServiceImpl")
public class ModelInstanceDefaultServiceImpl extends ServiceImpl<ModelInstanceDefaultMapper,ModelInstanceDefault> implements ModelInstanceDefaultService {

    @Override
    public Map<ModelType, ModelInstanceDefault> getAllDefaultUnderType() {
        List<ModelInstanceDefault> modelInstanceDefaults = list();
        Map<ModelType, ModelInstanceDefault> collect = modelInstanceDefaults.stream().collect(Collectors.toMap(ModelInstanceDefault::getModelType, modelInstanceDefault -> modelInstanceDefault));

        return collect;
    }

    @Override
    public boolean checkExistUnderType(ModelType modelType) {
        LambdaQueryWrapper<ModelInstanceDefault> modelInstanceDefaultLambdaQueryWrapper = new LambdaQueryWrapper<ModelInstanceDefault>()
                .eq(ModelInstanceDefault::getModelType, modelType);

        long count = count(modelInstanceDefaultLambdaQueryWrapper);

        return count > 0;
    }

    @Override
    public boolean checkIsDefault(Long modelInstanceId) {
        LambdaQueryWrapper<ModelInstanceDefault> modelInstanceDefaultLambdaQueryWrapper = new LambdaQueryWrapper<ModelInstanceDefault>()
                .eq(ModelInstanceDefault::getModelInstanceId, modelInstanceId);

        long count = count(modelInstanceDefaultLambdaQueryWrapper);
        return count > 0;
    }

    @Override
    public void addDefault(ModelInstanceDefault modelInstanceDefault) {
        boolean b = checkExistUnderType(modelInstanceDefault.getModelType());
        if (b) {
            throw new RuntimeException("该模型类型已存在默认实例");
        }

        save(modelInstanceDefault);
    }

    @Override
    public void deleteDefault(Long modelInstanceId, ModelType modelType) {

        LambdaQueryWrapper<ModelInstanceDefault> modelInstanceDefaultLambdaQueryWrapper = new LambdaQueryWrapper<ModelInstanceDefault>()
                .eq(ModelInstanceDefault::getModelInstanceId, modelInstanceId);
        remove(modelInstanceDefaultLambdaQueryWrapper);


    }

    @Override
    public void updateDefault(ModelInstanceDefault modelInstanceDefault) {
        if (!checkExistUnderType(modelInstanceDefault.getModelType())) {
            addDefault(modelInstanceDefault);
            return;
        }

        LambdaUpdateWrapper<ModelInstanceDefault> modelInstanceDefaultLambdaUpdateWrapper = new LambdaUpdateWrapper<ModelInstanceDefault>()
                .eq(ModelInstanceDefault::getModelType, modelInstanceDefault.getModelType())
                .set(ModelInstanceDefault::getModelInstanceId, modelInstanceDefault.getModelInstanceId());


        update(modelInstanceDefaultLambdaUpdateWrapper);
    }
}
