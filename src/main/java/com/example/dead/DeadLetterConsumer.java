package com.example.dead;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;

//@Component
@Slf4j
public class DeadLetterConsumer {
//    @RabbitListener(queues = RabbitConfig.DEAD_LETTER_QUEUE)
    public void receive(Message message, Channel channel) throws IOException {
        Order order = JSON.parseObject(message.getBody(), Order.class);
        log.info("收到死信消息：{}", order);
        log.info("死信消息附带的头信息： {}", JSON.toJSONString(message.getMessageProperties().getHeaders()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        /**
         * 1.死信消息附带的头信息：异常拒绝
         * {
         *   "x-first-death-exchange": "business.exchange",
         *   "x-death": [
         *     {
         *       "reason": "rejected",
         *       "count": 1,
         *       "exchange": "business.exchange",
         *       "time": 1669478090000,
         *       "routing-keys": [
         *         "rk.001"
         *       ],
         *       "queue": "business.queue"
         *     }
         *   ],
         *   "x-first-death-reason": "rejected",
         *   "x-first-death-queue": "business.queue"
         * }
         *
         * 2.死信消息附带的头信息：ttl超时
         * {"x-first-death-exchange":"business.exchange","x-death":
         * [
         *   {
         *     "reason": "expired",
         *     "original-expiration": "5000",
         *     "count": 1,
         *     "exchange": "business.exchange",
         *     "time": 1669480682000,
         *     "routing-keys": [
         *       "rk.001"
         *     ],
         *     "queue": "business.queue"
         *   }
         * ]
         *
         * 3.死信消息附带的头信息： 队列长度超出限制
         * {"x-first-death-exchange":"business.exchange","x-death":
         * [
         *   {
         *     "reason": "maxlen",
         *     "count": 1,
         *     "exchange": "business.exchange",
         *     "time": 1669482612000,
         *     "routing-keys": [
         *       "rk.001"
         *     ],
         *     "queue": "business.queue"
         *   }
         * ]
         */
    }
}
