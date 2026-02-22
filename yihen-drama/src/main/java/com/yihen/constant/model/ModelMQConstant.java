package com.yihen.constant.model;


public class ModelMQConstant {
    /** 一个 Topic Exchange 统一承载 model 相关事件 */
    public static final String MODEL_EXCHANGE = "model.exchange";

    /** 三类 routingKey：建议用分层命名，便于通配 */
    public static final String MODEL_INSTANCE_ADD_KEY    = "model.instance.add";
    public static final String MODEL_INSTANCE_UPDATE_KEY    = "model.instance.update";
    public static final String MODEL_INSTANCE_DELETE_KEY    = "model.instance.delete";
    public static final String MODEL_INSTANCE_LOAD_KEY = "model.instance.load";


    public static final String MODEL_DEFINITION_ADD_KEY    = "model.definition.add";
    public static final String MODEL_DEFINITION_UPDATE_KEY    = "model.definition.update";
    public static final String MODEL_DEFINITION_DELETE_KEY    = "model.definition.delete";
    public static final String MODEL_DEFINITION_LOAD_KEY = "model.definition.load";



    /** 四个队列：各自消费各自的业务（清晰、易扩展） */
    public static final String MODEL_INSTANCE_ADD_QUEUE    = "model.instance.add.queue";
    public static final String MODEL_INSTANCE_UPDATE_QUEUE = "model.instance.update.queue";
    public static final String MODEL_INSTANCE_DELETE_QUEUE = "model.instance.delete.queue";
    public static final String MODEL_INSTANCE_LOAD_QUEUE = "model.instance.load.queue";


    /** 三个队列：各自消费各自的业务（清晰、易扩展） */
    public static final String MODEL_DEFINITION_ADD_QUEUE    = "model.definition.add.queue";
    public static final String MODEL_DEFINITION_UPDATE_QUEUE = "model.definition.update.queue";
    public static final String MODEL_DEFINITION_DELETE_QUEUE = "model.definition.delete.queue";
    public static final String MODEL_DEFINITION_LOAD_QUEUE = "model.definition.load.queue";




}
