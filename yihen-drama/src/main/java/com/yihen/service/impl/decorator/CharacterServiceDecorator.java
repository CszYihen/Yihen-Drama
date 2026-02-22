package com.yihen.service.impl.decorator;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.constant.characters.CharactersMQConstant;
import com.yihen.constant.project.ProjectMQConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.controller.vo.ExtractionResultVO;
import com.yihen.controller.vo.ProjectCreateRequestVO;
import com.yihen.entity.Characters;
import com.yihen.entity.Project;
import com.yihen.enums.IdType;
import com.yihen.mapper.CharacterMapper;
import com.yihen.mapper.ProjectMapper;
import com.yihen.search.dto.CharacterDeleteDTO;
import com.yihen.service.CharacterService;
import com.yihen.service.ProjectService;
import com.yihen.util.LambdaFieldUtils;
import com.yihen.util.RedisUtils;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Primary // 让业务默认注入到“带缓存的实现”
@Slf4j
public class CharacterServiceDecorator extends ServiceImpl<CharacterMapper, Characters>  implements CharacterService {

    private final CharacterService characterService;            // 被装饰者
    private final RedisUtils redisUtils;
    private final RabbitTemplate rabbitTemplate ;

    public CharacterServiceDecorator(
            @Qualifier("characterServiceImpl") CharacterService characterService, RedisUtils redisUtils, RabbitTemplate rabbitTemplate) {
        this.characterService = characterService;
        this.redisUtils = redisUtils;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public List<Characters> getCharactersByEpisodeId(Long episodeId) {
        return characterService.getCharactersByEpisodeId(episodeId);
    }

    @Override
    public void deleteCharactersByEpisodeId(Long episodeId) {
        characterService.deleteCharactersByEpisodeId(episodeId);
        // 发送MQ同步消息 （ES）
        CharacterDeleteDTO characterDeleteDTO = new CharacterDeleteDTO();
        characterDeleteDTO.setId(episodeId);
        characterDeleteDTO.setType(IdType.EPISODE_ID);
        rabbitTemplate.convertAndSend(CharactersMQConstant.CHARACTER_INFO_EXCHANGE, CharactersMQConstant.CHARACTER_INFO_DELETE_KEY, characterDeleteDTO);
    }

    @Override
    public void updateCharacterInfo(Long id, String name, String description) {
        characterService.updateCharacterInfo(id, name, description);
        // 发送MQ同步消息 （ES）
        Characters characters = new Characters();
        characters.setId(id);
        rabbitTemplate.convertAndSend(CharactersMQConstant.CHARACTER_INFO_EXCHANGE, CharactersMQConstant.CHARACTER_INFO_UPDATE_KEY, characters);

    }

    @Override
    public Characters addCharacterInfo(Long episodeId, String name, String description) {
        Characters characters = characterService.addCharacterInfo(episodeId, name, description);
        // 发送MQ同步消息 （ES）
        rabbitTemplate.convertAndSend(CharactersMQConstant.CHARACTER_INFO_EXCHANGE, CharactersMQConstant.CHARACTER_INFO_ADD_KEY, characters);
        return characters;
    }

    @Override
    public void deleteCharacter(Long characterId) {
        characterService.deleteCharacter(characterId);
        // 发送MQ同步消息 （ES）
        CharacterDeleteDTO characterDeleteDTO = new CharacterDeleteDTO();
        characterDeleteDTO.setId(characterId);
        characterDeleteDTO.setType(IdType.ID);
        rabbitTemplate.convertAndSend(CharactersMQConstant.CHARACTER_INFO_EXCHANGE, CharactersMQConstant.CHARACTER_INFO_DELETE_KEY, characterDeleteDTO);

    }

    @Override
    public Characters upload(Long characterId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Characters characters = characterService.upload(characterId, file);

        // 发送MQ同步消息 （ES）
        rabbitTemplate.convertAndSend(CharactersMQConstant.CHARACTER_INFO_EXCHANGE, CharactersMQConstant.CHARACTER_INFO_UPDATE_KEY, characters);
        return characters;
    }

    @Override
    public List<Characters> getByProjectId(Long projectId, Page<Characters> charactersPage) {
        return characterService.getByProjectId(projectId, charactersPage);
    }


}
