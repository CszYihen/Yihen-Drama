package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.asyn.ScenePersistFacade;
import com.yihen.common.Result;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.controller.SceneController;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.mapper.SceneMapper;
import com.yihen.service.SceneService;
import com.yihen.util.MinioUtil;
import com.yihen.util.UrlUtils;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service("sceneServiceImpl")
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements SceneService {

    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private ScenePersistFacade scenePersistFacade;

    @Override
    public List<Scene> getScenesByEpisodeId(Long episodeId) {
        return list(new LambdaQueryWrapper<Scene>()
                .eq(Scene::getEpisodeId, episodeId));
    }

    @Override
    public void deleteScenesByEpisodeId(Long episodeId) {
        List<Long> ids = sceneMapper.getSceneIdsByEpisodeId(episodeId);
        ids.forEach(this::deleteScene);

    }



    @Override
    public Scene addScene(Long episodeId, String name, String description) {
        Long projectId = episodeMapper.getProjectIdByEpisodeId(episodeId);

        Scene scene = new Scene();
        scene.setName(name);
        scene.setDescription(description);
        scene.setProjectId(projectId);
        scene.setEpisodeId(episodeId);

        save(scene);

        return scene;
    }

    @Override
    public void updateScene(Long id, String name, String description) {
        lambdaUpdate().eq(Scene::getId, id)
                .set(Scene::getName, name)
                .set(Scene::getDescription, description).update();
    }

    @Override
    public void deleteScene(Long sceneId) {
        Scene scene = getById(sceneId);
        // 删除Minio中的数据
        if (!ObjectUtils.isEmpty(scene.getThumbnail())) {
            String objectName = scene.getThumbnail().replace(minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/", "");
            minioUtil.deleteObject(MinioConstant.BUCKET_NAME , objectName);
        }

        removeById(sceneId);

        // 删除关联内容
        scenePersistFacade.deleteSceneRelatedContentAsync(sceneId);

    }

    @Override
    public Scene upload(Long sceneId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Scene scene = getById(sceneId);

        // 生成保存路径
        String originalFilename = file.getOriginalFilename();
        String extension = UrlUtils.extractFileExtension(originalFilename);
        // 场景图片路径 /project/{projectId}/scenes/{charactersId-charactersName.xxx}
        String imgName = scene.getId() + "-" +scene.getName() + "." + extension;
        String objectName = MinioConstant.SCENE_IMG_PATH.formatted(scene.getProjectId(), imgName);



        // 修改场景图片url
        String thumbnail = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;

        // 校验：是否 Minio中存在图片
        if (!ObjectUtils.isEmpty(scene.getThumbnail()) && !scene.getThumbnail().equals(thumbnail)) {
            // 存在且不相同，则删除原来的
            String oldObjectName = scene.getThumbnail().replace(minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/", "");
            minioUtil.deleteObject(MinioConstant.BUCKET_NAME, oldObjectName);
        }

        scene.setThumbnail(thumbnail);
        updateById(scene);




        // 上传文件到Minio
        InputStream inputStream = file.getInputStream();
        long size = file.getSize();
        minioUtil.uploadFile(inputStream, (int) size,MinioConstant.BUCKET_NAME, objectName);
        return scene;

    }

    @Override
    public List<Scene> getByProjectId(Long projectId, Page<Scene> scenePage) {
        LambdaQueryWrapper<Scene> sceneLambdaQueryWrapper = new LambdaQueryWrapper<Scene>().eq(Scene::getProjectId, projectId);
        page(scenePage, sceneLambdaQueryWrapper);
        return scenePage.getRecords();
    }
}
