package com.yihen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface SceneService extends IService<Scene> {

    List<Scene> getScenesByEpisodeId(Long episodeId);

    // 根据章节id删除场景
    void deleteScenesByEpisodeId(Long episodeId);



    Scene addScene(Long episodeId, String name, String description);

    void updateScene(Long id, String name, String description);

    void deleteScene(Long sceneId);

    Scene upload(Long sceneId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    List<Scene> getByProjectId(Long projectId, Page<Scene> scenePage);
}
