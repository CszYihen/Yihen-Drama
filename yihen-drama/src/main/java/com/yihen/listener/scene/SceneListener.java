package com.yihen.listener.scene;


import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.yihen.constant.characters.CharactersMQConstant;
import com.yihen.constant.scene.SceneMQConstant;
import com.yihen.entity.Characters;
import com.yihen.entity.Scene;
import com.yihen.enums.IdType;
import com.yihen.mapper.CharacterMapper;
import com.yihen.mapper.SceneMapper;
import com.yihen.search.doc.CharactersDoc;
import com.yihen.search.doc.SceneDoc;
import com.yihen.search.dto.CharacterDeleteDTO;
import com.yihen.search.dto.SceneDeleteDTO;
import com.yihen.search.mapper.CharactersDocMapper;
import com.yihen.search.mapper.SceneDocMapper;
import com.yihen.search.repository.CharactersSearchRepository;
import com.yihen.search.repository.SceneSearchRepository;
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
public class SceneListener {
    @Autowired
    private SceneSearchRepository sceneSearchRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private ElasticsearchOperations operations;


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = SceneMQConstant.SCENE_INFO_ADD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = SceneMQConstant.SCENE_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = SceneMQConstant.SCENE_INFO_ADD_KEY
            )
    )
    public void addScene(Scene scene) {
        //  ES同步
        sceneSearchRepository.save(SceneDocMapper.toDoc(scene));

    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = SceneMQConstant.SCENE_INFO_DELETE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = SceneMQConstant.SCENE_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = SceneMQConstant.SCENE_INFO_DELETE_KEY
            )
    )
    public void deleteScene(SceneDeleteDTO sceneDeleteDTO) {

        //  ES同步
        if (sceneDeleteDTO.getType().equals(IdType.ID)) {
            // 根据角色id删除
            sceneSearchRepository.deleteById(sceneDeleteDTO.getId());
        }else if (sceneDeleteDTO.getType().equals(IdType.EPISODE_ID)) {
            // 1) term 精确匹配 episodeId
            Query q = new Query.Builder()
                    .term(t -> t.field("episodeId").value(sceneDeleteDTO.getId()))
                    .build();

            // 2) 组装 NativeQuery
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q)
                    .build();

            // 3) 执行 deleteByQuery（批量删除）
            operations.delete(query, SceneDoc.class);
        }

    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = SceneMQConstant.SCENE_INFO_UPDATE_QUEUE, durable = "true"),
                    exchange = @Exchange(name = SceneMQConstant.SCENE_INFO_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = SceneMQConstant.SCENE_INFO_UPDATE_KEY
            )
    )
    public void updateScene(Scene scene) {
        if (ObjectUtils.isEmpty(scene.getThumbnail())) {
            scene = sceneMapper.selectById(scene.getId());
        }
        //  ES同步
        sceneSearchRepository.save(SceneDocMapper.toDoc(scene));
    }

}
