package com.example.test2;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


/**
 * 监听自动确认订单
 * @author qzz
 */
@Component
@Slf4j
public class ConfirmOrderListener {


    /**
     * 接受消息
     * @param channel
     * @param message
     * @throws Exception
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitMQConfig.CONFIRM_ORDER)
    public void receiverMsg(Channel channel, Message message) throws Exception {
        //body 即消息体
        String msg = new String(message.getBody());
        String messageId = message.getMessageProperties().getMessageId();
        log.info("【消费者】 消息内容：【{}】。messageId 【{}】",msg, messageId);

        try{
            //如果有业务逻辑，则在这里编写


            //告诉服务器收到这条消息 无需再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            log.error("消息处理出现异常：{}",e.getMessage());
            //告诉消息服务器 消息处理异常，消息需要重新再次发送！
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false, true);
        }
    }
}
