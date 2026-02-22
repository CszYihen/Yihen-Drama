package com.yihen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.Characters;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface CharacterService extends IService<Characters> {

    List<Characters> getCharactersByEpisodeId(Long episodeId);

    // 根据章节id删除角色
    void deleteCharactersByEpisodeId(Long episodeId);




    void updateCharacterInfo(Long id, String name, String description);

    Characters addCharacterInfo(Long episodeId, String name, String description);

    void deleteCharacter(Long characterId);

    Characters upload(Long characterId, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    List<Characters> getByProjectId(Long projectId, Page<Characters> charactersPage);
}
