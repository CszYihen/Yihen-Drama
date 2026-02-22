package com.yihen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihen.asyn.CharacterPersistFacade;
import com.yihen.config.properties.MinioProperties;
import com.yihen.constant.MinioConstant;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.entity.StoryBoardCharacter;
import com.yihen.entity.VideoTask;
import com.yihen.enums.TaskType;
import com.yihen.mapper.CharacterMapper;
import com.yihen.mapper.EpisodeMapper;
import com.yihen.mapper.VideoTaskMapper;
import com.yihen.service.CharacterService;
import com.yihen.service.StoryBoardCharacterService;
import com.yihen.service.VideoTaskService;
import com.yihen.util.MinioUtil;
import com.yihen.util.UrlUtils;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service("characterServiceImpl")
public class CharacterServiceImpl extends ServiceImpl<CharacterMapper, Characters> implements CharacterService {


    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private VideoTaskMapper videoTaskMapper;

    @Autowired
    private CharacterMapper characterMapper;

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private StoryBoardCharacterService  storyBoardCharacterService;

    @Autowired
    private CharacterPersistFacade characterPersistFacade;

    @Override
    public List<Characters> getCharactersByEpisodeId(Long episodeId) {
        return list(new LambdaQueryWrapper<Characters>()
                .eq(Characters::getEpisodeId, episodeId));
    }

    @Override
    public void deleteCharactersByEpisodeId(Long episodeId) {
        List<Long> ids = characterMapper.getCharacterIdsByEpisodeId(episodeId);
        // 删除角色
        ids.forEach(this::deleteCharacter);

    }


    @Override
    public void updateCharacterInfo(Long id, String name, String description) {
        lambdaUpdate().eq(Characters::getId, id)
                .set(Characters::getName, name)
                .set(Characters::getDescription, description).update();
    }

    @Override
    public Characters addCharacterInfo(Long episodeId, String name, String description) {
        Long projectId = episodeMapper.getProjectIdByEpisodeId(episodeId);

        Characters characters = new Characters();
        characters.setName(name);
        characters.setDescription(description);
        characters.setProjectId(projectId);
        characters.setEpisodeId(episodeId);

        save(characters);

        return characters;

    }

    @Override
    public void deleteCharacter(Long characterId) {
        Characters characters = getById(characterId);
        // 删除Minio中的数据
        if (!ObjectUtils.isEmpty(characters.getAvatar())) {
            String objectName = characters.getAvatar().replace(minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/", "");
            minioUtil.deleteObject(MinioConstant.BUCKET_NAME , objectName);
        }

        removeById(characterId);
        // 删除关联数据
        characterPersistFacade.deleteCharacterRelatedContentAsync(characterId);

    }


    @Override
    public Characters upload(Long characterId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Characters characters = getById(characterId);

        // 生成保存路径
        String originalFilename = file.getOriginalFilename();
        String extension = UrlUtils.extractFileExtension(originalFilename);
        // 角色图片路径 /project/{projectId}/characters/{charactersId-charactersName.xxx}
        String imgName = characters.getId() + "-" +characters.getName() + "." + extension;
        String objectName = MinioConstant.CHARACTER_IMG_PATH.formatted(characters.getProjectId(), imgName);



        // 修改场景图片url
        String avatar = minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;

        // 校验：是否 Minio中存在图片
        if (!ObjectUtils.isEmpty(characters.getAvatar()) && !characters.getAvatar().equals(avatar)) {
            // 存在且不相同，则删除原来的
            String oldObjectName = characters.getAvatar().replace(minioProperties.getEndPoint() + "/" + MinioConstant.BUCKET_NAME + "/", "");
            minioUtil.deleteObject(MinioConstant.BUCKET_NAME, oldObjectName);
        }

        characters.setAvatar(avatar);
        updateById(characters);


        // 上传文件到Minio
        InputStream inputStream = file.getInputStream();
        long size = file.getSize();
        minioUtil.uploadFile(inputStream, (int) size,MinioConstant.BUCKET_NAME, objectName);
        return characters;

    }

    @Override
    public List<Characters> getByProjectId(Long projectId, Page<Characters> charactersPage) {
        LambdaQueryWrapper<Characters> charactersLambdaQueryWrapper = new LambdaQueryWrapper<Characters>().eq(Characters::getProjectId, projectId);
        page(charactersPage, charactersLambdaQueryWrapper);
        return charactersPage.getRecords();
    }
}
