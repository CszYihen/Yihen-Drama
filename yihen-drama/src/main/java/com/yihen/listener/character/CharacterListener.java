package com.yihen.listener.character;


import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.yihen.constant.characters.CharactersMQConstant;
import com.yihen.constant.model.ModelMQConstant;
import com.yihen.constant.model.ModelManageRedisConstant;
import com.yihen.entity.Characters;
import com.yihen.entity.ModelDefinition;
import com.yihen.entity.ModelInstance;
import com.yihen.enums.IdType;
import com.yihen.enums.ModelType;
import com.yihen.init.ModelCacheInitializer;
import com.yihen.mapper.CharacterMapper;
import com.yihen.mapper.ProjectMapper;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.dto.CharacterDeleteDTO;
import com.yihen.search.mapper.CharactersDocMapper;
import com.yihen.search.mapper.ProjectDocMapper;
import com.yihen.search.repository.CharactersSearchRepository;
import com.yihen.search.repository.ProjectSearchRepository;
import com.yihen.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class CharacterListener {
    @Autowired
    private CharactersSearchRepository charactersSearchRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CharacterMapper characterMapper;

    @Autowired
    private ElasticsearchOperations operations;


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = CharactersMQConstant.CHARACTER_INFO_ADD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = CharactersMQConstant.CHARACTER_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = CharactersMQConstant.CHARACTER_INFO_ADD_KEY
            )
    )
    public void addCharacter(Characters characters) {
        //  ES同步
        charactersSearchRepository.save(CharactersDocMapper.toDoc(characters));

    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = CharactersMQConstant.CHARACTER_INFO_DELETE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = CharactersMQConstant.CHARACTER_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = CharactersMQConstant.CHARACTER_INFO_DELETE_KEY
            )
    )
    public void deleteCharacter(CharacterDeleteDTO characterDeleteDTO) {

        //  ES同步
        if (characterDeleteDTO.getType().equals(IdType.ID)) {
            // 根据角色id删除
            charactersSearchRepository.deleteById(characterDeleteDTO.getId());
        }else if (characterDeleteDTO.getType().equals(IdType.EPISODE_ID)) {
            // 1) term 精确匹配 episodeId
            Query q = new Query.Builder()
                    .term(t -> t.field("episodeId").value(characterDeleteDTO.getId()))
                    .build();

            // 2) 组装 NativeQuery
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q)
                    .build();

            // 3) 执行 deleteByQuery（批量删除）
            operations.delete(query, CharactersDoc.class);
        }

    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = CharactersMQConstant.CHARACTER_INFO_UPDATE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = CharactersMQConstant.CHARACTER_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = CharactersMQConstant.CHARACTER_INFO_UPDATE_KEY
            )
    )
    public void updateCharacter(Characters characters) {
        if (ObjectUtils.isEmpty(characters.getAvatar())) {
            characters = characterMapper.selectById(characters.getId());
        }
        //  ES同步
        charactersSearchRepository.save(CharactersDocMapper.toDoc(characters));
    }

}
