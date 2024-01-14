package com.example.dead;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

//@Configuration
@Data
public class RabbitConfig {
    public static final String DEAD_LETTER_ROUTING_KEY = "rk.dead.letter.001";
    public static final String BUSINESS_ROUTING_KEY = "rk.001";
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    public static final String BUSINESS_QUEUE = "business.queue";
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
    public static final String BUSINESS_EXCHANGE = "business.exchange";

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    /**
     * 声明业务队列的交换机
     */
    @Bean("businessExchange")
    public DirectExchange businessExchange() {
        return new DirectExchange(BUSINESS_EXCHANGE);
    }

    /**
     * 声明死信交换机
     */
    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    /**
     * 声明业务队列
     */
    @Bean("businessQueue")
    public Queue businessQueue() {
        Map<String, Object> args = new HashMap<>(16);
        // 设置当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        // 设置当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        // 设置消息的过期时间 单位：ms（毫秒）
//        args.put("x-message-ttl", 5000);
        args.put("x-max-length", 5);
        // 指定队列中消息达到最大限制之后的行为
        // 可选参数有：
        // drop-head（删除队列头部的消息）、
        // reject-publish（最近发来的消息将被丢弃）、
        // reject-publish-dlx（拒绝发送消息到死信交换器）
        // 注意，类型为quorum的队列只支持drop-head
        // x-overflow属性默认的处理策略是丢掉队列的头部的消息，或者将队列头部的消息投递到死信交换机
        // args.put("x-overflow", "reject-publish");
        return QueueBuilder.durable(BUSINESS_QUEUE).withArguments(args).build();
    }

    /**
     * 声明死信队列
     */
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    /**
     * 声明业务队列和业务交换机的绑定关系
     */
    @Bean
    public Binding businessBinding(@Qualifier("businessQueue") Queue queue,
                                   @Qualifier("businessExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(BUSINESS_ROUTING_KEY);
    }

    /**
     * 声明死信队列和死信交换机的绑定关系
     */
    @Bean
    public Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue queue,
                                     @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_ROUTING_KEY);
    }
}
