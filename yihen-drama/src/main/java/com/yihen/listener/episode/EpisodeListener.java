package com.yihen.listener.episode;

import com.yihen.constant.episode.EpisodeMQConstant;
import com.yihen.constant.project.ProjectMQConstant;
import com.yihen.constant.project.ProjectRedisConstant;
import com.yihen.entity.Episode;
import com.yihen.entity.Project;
import com.yihen.search.mapper.ProjectDocMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EpisodeListener {


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = EpisodeMQConstant.EPISODE_VECTOR_ADD_QUEUE, durable = "true"),
                    exchange = @Exchange(name = EpisodeMQConstant.EPISODE_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = EpisodeMQConstant.EPISODE_VECTOR_ADD_KEY
            )
    )
    public void splitEpisodeToVector(Episode episode) {
        // TODO 切分章节
    }



}
