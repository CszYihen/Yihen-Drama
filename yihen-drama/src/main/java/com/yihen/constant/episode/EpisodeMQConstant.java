package com.yihen.constant.episode;

public class EpisodeMQConstant {

    /** 一个 Topic Exchange 统一承载 project 相关事件 */
    public static final String EPISODE_EXCHANGE = "episode.exchange";

    /** 三类 routingKey：建议用分层命名，便于通配 */
    public static final String EPISODE_VECTOR_ADD_KEY    = "episode.vector.add";

    /** 三个队列：各自消费各自的业务（清晰、易扩展） */
    public static final String EPISODE_VECTOR_ADD_QUEUE    = "episode.vector.add.queue";


}
