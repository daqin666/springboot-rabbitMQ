package com.example.dead;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Slf4j
public class BusinessConsumer {
    @RabbitListener(queues = RabbitConfig.BUSINESS_QUEUE)
    public void receive(Message message, Channel channel) throws IOException {
        Order order = JSON.parseObject(message.getBody(), Order.class);
        log.info("收到业务消息：{}", order);
        log.info("业务消息附带的头信息： {}", JSON.toJSONString(message.getMessageProperties().getHeaders()));
        try {
            if (StringUtils.isEmpty(order.getStatus())) {
                throw new IllegalArgumentException("order's status can not be null!");
            }
        } catch (Exception e) {
            log.error("业务消息消费失败：{}", e.getMessage());
            // 消息消费异常后，返回一个nack响应
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
