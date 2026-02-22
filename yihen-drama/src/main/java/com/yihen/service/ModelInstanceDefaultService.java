package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.ModelInstanceDefault;
import com.yihen.enums.ModelType;

import java.util.Map;

public interface ModelInstanceDefaultService extends IService<ModelInstanceDefault> {

    // 获取各类型默认实例
    Map<ModelType, ModelInstanceDefault> getAllDefaultUnderType();


    // 查询该类型是否存在默认实例
    boolean checkExistUnderType(ModelType modelType);

    // 检查是否为默认实例
    boolean checkIsDefault(Long modelInstanceId);

    // 添加默认实例
    void addDefault(ModelInstanceDefault modelInstanceDefault);

    // 删除默认实例
    void deleteDefault(Long modelInstanceId, ModelType modelType);

    // 更新默认实例
    void updateDefault(ModelInstanceDefault modelInstanceDefault);


}
