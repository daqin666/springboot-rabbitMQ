package com.example.test1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Producer {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public Object hello() {

        // 向 EXCHANGE_ARTICLE 发送一条消息，并且指定相应的路由规则
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.download", "去GridFS下载文章的HTML吧~~");

        return "OK";
    }

    public Object hello2() {

        // 构造消息（将消息持久化）
        Message message = MessageBuilder.withBody("单程车票".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();

        // 向 EXCHANGE_ARTICLE 发送一条消息，并且指定相应的路由规则
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.download", message);

//        // 设置消息确认回调方法
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            /**
//             * MQ确认回调方法
//             * @param correlationData 消息的唯一标识
//             * @param ack 消息是否成功收到
//             * @param cause 失败原因
//             */
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                // 记录日志
//                log.info("ConfirmCallback...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
//                if (!ack) {
//                    // 出错处理
//
//                }
//            }
//        });
//
//        // 设置路由失败回调方法
//        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//            /**
//             * MQ没有将消息投递给指定的队列回调方法
//             * @param message 投递失败的消息详细信息
//             * @param replyCode 回复的状态码
//             * @param replyText 回复的文本内容
//             * @param exchange 消息发给哪个交换机
//             * @param routingKey 消息用哪个路邮键
//             */
//            @Override
//            public void returnedMessage(Message message, int replyCode, String replyText,
//                                        String exchange, String routingKey) {
//                // 记录日志
//                log.info("Fail Message["+message+"]==>replyCode["+replyCode+"]" +
//                        "==>replyText["+replyText+"]==>exchange["+exchange+
//                        "]==>routingKey["+routingKey+"]");
//                // 出错处理
//
//            }
//        });

        return "OK";
    }

}
