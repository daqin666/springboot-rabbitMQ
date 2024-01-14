package com.example.test1;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;

@Slf4j
public class Consumer {

    /**
     * 监听消息队列
     * @param payload 消息内容
     * @param message 消息对象
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_HTML})
    public void watchQueue(String payload, Message message) {
        System.out.println(payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.publish.download.do")) {
            System.out.println("article.publish.download.do");
        } else if (routingKey.equalsIgnoreCase("article.success.do")) {
            System.out.println("article.success.do");
        } else {
            System.out.println("不符合的规则：" + routingKey);
        }
    }

    /**
     * 监听消息队列
     * @param message 消息内容
     * @param channel channel
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_HTML})
    public void watchQueue2(Message message, Channel channel) {
        // 获取消息索引
        long index = message.getMessageProperties().getDeliveryTag();
        // 解析消息
        byte[] body = message.getBody();

        /**
         * void basicAck(long deliveryTag, boolean multiple) 方法（会抛异常）：
         * deliveryTag：该消息的index
         * multiple：是否批量处理（true 表示将一次性ack所有小于deliveryTag的消息）
         *
         * void basicNack(long deliveryTag, boolean multiple, boolean requeue) 方法（会抛异常）：
         * deliveryTag：该消息的index
         * multiple：是否批量处理（true 表示将一次性ack所有小于deliveryTag的消息）
         * requeue：被拒绝的是否重新入队列（true 表示添加在队列的末端；false 表示丢弃）
         */

        try {
            // 业务处理

            // 业务执行成功则手动确认
            channel.basicAck(index, false);
        }catch (Exception e) {
            // 记录日志
            log.info("出现异常：{}", e.getMessage());
            try {
                // 手动丢弃信息
                channel.basicNack(index, false, false);
            } catch (IOException ex) {
                log.info("丢弃消息异常");
            }
        }

    }
}
