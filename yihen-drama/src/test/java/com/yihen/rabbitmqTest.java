package com.yihen;

import com.yihen.search.repository.ProjectSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class rabbitmqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testSend() {
        String queueName = "hello.queue";
        String msg = "hello,spring amqp!";
        rabbitTemplate.convertAndSend(queueName, msg);
    }


    @Test
    public void testWorkQueue() throws InterruptedException {
        // 队列名称
        String queueName = "work.queue";
        // 消息
        String message = "hello, message_";
        for (int i = 0; i < 50; i++) {
            // 发送消息，每20毫秒发送一次，相当于每秒发送50条消息
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);
        }
    }


    @Test
    public void testFanoutExchange() {
        // 交换机名称
        String exchangeName = "yihen.fanout";
        // 消息
        String message = "hello, everyone!";
        // 由于交换机类型是fanout，routeKey参数可以不写
        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }

    @Test
    public void testSendDirectExchange() {
        // 交换机名称
        String exchangeName = "yihen.direct";
        // 消息
        String message = "红色警报！日本乱排核废水，导致海洋生物变异，惊现哥斯拉！";
        // 发送消息，指定Routing Key为red，此时direct.queue1和direct.queue2都能接收到消息
        rabbitTemplate.convertAndSend(exchangeName, "red", message);
        // 发送消息，指定Routing Key为blue，此时direct.queue1才能接收到消息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message);
        // 发送消息，指定Routing Key为yellow，此时direct.queue2才能接收到消息
        rabbitTemplate.convertAndSend(exchangeName, "yellow", message);
    }
}
