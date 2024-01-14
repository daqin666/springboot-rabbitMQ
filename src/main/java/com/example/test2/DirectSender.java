package com.example.test2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Routing 路由模型（交换机类型：direct）
 * 消息生成者
 * @author qzz
 */
@Component
public class DirectSender {

    private static final Logger log = LoggerFactory.getLogger(DirectSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param routingKey
     * @param msg
     */
    public void send (String routingKey,String msg){
        Message message = MessageBuilder.withBody(msg.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8")
                .setMessageId(UUID.randomUUID()+"").build();

        log.info("【发送者】消息内容【{}】 交换机【{}】 路由【{}】 消息ID【{}】",msg,RabbitMQConfig.DIRECT_EXCHANGE
                ,routingKey,message.getMessageProperties().getMessageId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE,routingKey,message);
    }
}
