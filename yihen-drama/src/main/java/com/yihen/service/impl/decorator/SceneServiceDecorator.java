package com.yihen.service.impl.decorator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.constant.characters.CharactersMQConstant;
import com.yihen.constant.scene.SceneMQConstant;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.enums.IdType;
import com.yihen.mapper.CharacterMapper;
import com.yihen.mapper.SceneMapper;
import com.yihen.search.dto.CharacterDeleteDTO;
import com.yihen.search.dto.SceneDeleteDTO;
import com.yihen.service.CharacterService;
import com.yihen.service.SceneService;
import com.yihen.util.RedisUtils;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@Primary // 让业务默认注入到“带缓存的实现”
@Slf4j
public class SceneServiceDecorator extends ServiceImpl<SceneMapper, Scene>  implements SceneService {

    private final SceneService sceneService;            // 被装饰者
    private final RedisUtils redisUtils;
    private final RabbitTemplate rabbitTemplate ;

    public SceneServiceDecorator(
            @Qualifier("sceneServiceImpl") SceneService sceneService, RedisUtils redisUtils, RabbitTemplate rabbitTemplate) {
        this.sceneService = sceneService;
        this.redisUtils = redisUtils;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public List<Scene> getScenesByEpisodeId(Long episodeId) {

        return sceneService.getScenesByEpisodeId(episodeId);
    }

    @Override
    public void deleteScenesByEpisodeId(Long episodeId) {
        sceneService.deleteScenesByEpisodeId(episodeId);
        // 发送MQ同步消息 （ES）
        SceneDeleteDTO sceneDeleteDTO = new SceneDeleteDTO();
        sceneDeleteDTO.setId(episodeId);
        sceneDeleteDTO.setType(IdType.EPISODE_ID);
        rabbitTemplate.convertAndSend(SceneMQConstant.SCENE_INFO_EXCHANGE, SceneMQConstant.SCENE_INFO_DELETE_KEY, sceneDeleteDTO);

    }

    @Override
    public Scene addScene(Long episodeId, String name, String description) {
        Scene scene = sceneService.addScene(episodeId, name, description);
        // 发送MQ同步消息 （ES）
        rabbitTemplate.convertAndSend(SceneMQConstant.SCENE_INFO_EXCHANGE, SceneMQConstant.SCENE_INFO_ADD_KEY, scene);

        return scene;
    }

    @Override
    public void updateScene(Long id, String name, String description) {
        sceneService.updateScene(id, name, description);
        // 发送MQ同步消息 （ES）
        Scene scene = new Scene();
        scene.setId(id);
        rabbitTemplate.convertAndSend(SceneMQConstant.SCENE_INFO_EXCHANGE, SceneMQConstant.SCENE_INFO_UPDATE_KEY, scene);

    }

    @Override
    public void deleteScene(Long sceneId) {
        sceneService.deleteScene(sceneId);

        // 发送MQ同步消息 （ES）
        SceneDeleteDTO sceneDeleteDTO = new SceneDeleteDTO();
        sceneDeleteDTO.setId(sceneId);
        sceneDeleteDTO.setType(IdType.ID);
        rabbitTemplate.convertAndSend(SceneMQConstant.SCENE_INFO_EXCHANGE, SceneMQConstant.SCENE_INFO_DELETE_KEY, sceneDeleteDTO);


    }

    @Override
    public Scene upload(Long sceneId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Scene scene = sceneService.upload(sceneId, file);

        // 发送MQ同步消息 （ES）
        rabbitTemplate.convertAndSend(SceneMQConstant.SCENE_INFO_EXCHANGE, SceneMQConstant.SCENE_INFO_UPDATE_KEY, scene);

        return scene;
    }

    @Override
    public List<Scene> getByProjectId(Long projectId, Page<Scene> scenePage) {
        return sceneService.getByProjectId(projectId, scenePage);
    }
}
