package com.yihen.constant;


public class MinioConstant {
    // 存储桶名称
    public static final String BUCKET_NAME = "yihen-drama";

    // 角色图片路径 /project/{projectId}/characters/{charactersId-charactersName.xxx}
    public static final String CHARACTER_IMG_PATH = "project/%s/characters/%s";

    // 场景图片路径 /project/{projectId}/scenes/{charactersId-charactersName.xxx}
    public static final String SCENE_IMG_PATH = "project/%s/scenes/%s";

    //  分镜图地址 /project/{projectId}/shots/episode-{episodeId}/{shotId-shotIndex.xxx}
    public static final String SHOT_IMG_PATH = "project/%s/shots/episode-%s/%s";
}
