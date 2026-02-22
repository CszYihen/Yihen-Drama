package com.yihen.constant.characters;

public class CharactersMQConstant {

    /** 一个 Topic Exchange 统一承载 project 相关事件 */
    public static final String CHARACTER_INFO_EXCHANGE = "character.info.exchange";

    /** 三类 routingKey：建议用分层命名，便于通配 */
    public static final String CHARACTER_INFO_ADD_KEY    = "character.info.add";
    public static final String CHARACTER_INFO_UPDATE_KEY = "character.info.update";
    public static final String CHARACTER_INFO_DELETE_KEY = "character.info.delete";

    /** 三个队列：各自消费各自的业务（清晰、易扩展） */
    public static final String CHARACTER_INFO_ADD_QUEUE    = "character.info.add.queue";
    public static final String CHARACTER_INFO_UPDATE_QUEUE = "character.info.update.queue";
    public static final String CHARACTER_INFO_DELETE_QUEUE = "character.info.delete.queue";


}
