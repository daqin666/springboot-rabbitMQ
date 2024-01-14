package com.example.dead;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BusinessProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 消息异常，channel.basicNack方法响应
     * @param order
     */
    public void send(Order order) {
        rabbitTemplate.convertAndSend(RabbitConfig.BUSINESS_EXCHANGE, RabbitConfig.BUSINESS_ROUTING_KEY,
                JSONObject.toJSONString(order));
    }

    /**
     * ttl时间超时
     * @param order
     */
    public void send2(Order order) {
        MessagePostProcessor messagePostProcessor = message -> {
            // 设置消息过期时间为5秒
            message.getMessageProperties().setExpiration("5000");
            return message;
        };
        log.info("开始发送业务消息");
        rabbitTemplate.convertAndSend(RabbitConfig.BUSINESS_EXCHANGE, RabbitConfig.BUSINESS_ROUTING_KEY,
                JSON.toJSONString(order), messagePostProcessor);
    }
}
