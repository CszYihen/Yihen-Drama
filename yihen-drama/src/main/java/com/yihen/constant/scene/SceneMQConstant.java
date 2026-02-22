package com.yihen.constant.scene;

public class SceneMQConstant {

    /** 一个 Topic Exchange 统一承载 project 相关事件 */
    public static final String SCENE_INFO_EXCHANGE = "scene.info.exchange";

    /** 三类 routingKey：建议用分层命名，便于通配 */
    public static final String SCENE_INFO_ADD_KEY    = "scene.info.add";
    public static final String SCENE_INFO_UPDATE_KEY = "scene.info.update";
    public static final String SCENE_INFO_DELETE_KEY = "scene.info.delete";

    /** 三个队列：各自消费各自的业务（清晰、易扩展） */
    public static final String SCENE_INFO_ADD_QUEUE    = "scene.info.add.queue";
    public static final String SCENE_INFO_UPDATE_QUEUE = "scene.info.update.queue";
    public static final String SCENE_INFO_DELETE_QUEUE = "scene.info.delete.queue";


}
