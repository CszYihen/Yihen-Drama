package com.yihen.constant.project;


public class ProjectMQConstant {
    /** 一个 Topic Exchange 统一承载 project 相关事件 */
    public static final String PROJECT_INFO_EXCHANGE = "project.info.exchange";

    /** 三类 routingKey：建议用分层命名，便于通配 */
    public static final String PROJECT_INFO_ADD_KEY    = "project.info.add";
    public static final String PROJECT_INFO_UPDATE_KEY = "project.info.update";
    public static final String PROJECT_INFO_DELETE_KEY = "project.info.delete";
    public static final String PROJECT_INFO_LOAD_KEY = "project.info.load";

    /** 三个队列：各自消费各自的业务（清晰、易扩展） */
    public static final String PROJECT_INFO_ADD_QUEUE    = "project.info.add.queue";
    public static final String PROJECT_INFO_UPDATE_QUEUE = "project.info.update.queue";
    public static final String PROJECT_INFO_DELETE_QUEUE = "project.info.delete.queue";
    public static final String PROJECT_INFO_LOAD_QUEUE = "project.info.load.queue";

    /** 也可以给个通配：监听所有事件（可用于日志/审计/重放） */
    public static final String PROJECT_INFO_ALL_KEY = "project.info.*";
    public static final String PROJECT_INFO_ALL_QUEUE = "project.info.all.queue";


}
