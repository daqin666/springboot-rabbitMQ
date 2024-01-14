package com.example.test2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@Slf4j
public class RabbitMQConfig {
    /**
     * 交换机名称
     */
    public static final String DIRECT_EXCHANGE = "directExchange";

    /**
     * 取消订单 队列名称 routingkey
     */
    public static final String CANCEL_ORDER = "cancel-order";

    /**
     * 自动确认订单 队列名称\routingkey
     */
    public static final String CONFIRM_ORDER = "confirm-order";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        /**
         * 比较常用的 Converter 就是 Jackson2JsonMessageConverter,在发送消息时，它会先将自定义的消息类序列化成json格式，
         * 再转成byte构造 Message，在接收消息时，会将接收到的 Message 再反序列化成自定义的类
         */
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //开启手动ACK
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(){
        rabbitTemplate.setEncoding("UTF-8");
        rabbitTemplate.setMandatory(true);
        /**
         * ReturnsCallback消息没有正确到达队列时触发回调，如果正确到达队列不执行
         * config : 需要开启rabbitmq发送失败回退
         * yml配置publisher-returns: true
         * 或rabbitTemplate.setMandatory(true);设置为true
         */
//        rabbitTemplate.setReturnsCallback(returnedMessage -> {
//            String messageId = returnedMessage.getMessage().getMessageProperties().getMessageId();
//            byte[] message = returnedMessage.getMessage().getBody();
//            Integer replyCode = returnedMessage.getReplyCode();
//            String replyText = returnedMessage.getReplyText();
//            String exchange = returnedMessage.getExchange();
//            String routingKey = returnedMessage.getRoutingKey();
//
//            log.info("消息：{} 发送失败，消息ID：{} 应答码：{} 原因：{} 交换机：{} 路由键：{}",
//                    new String(message),messageId,replyCode,replyText,exchange,routingKey);
//
//        });

        return rabbitTemplate;
    }

    /**
     * 声明直连交换机  支持持久化
     * @return
     */
    @Bean(DIRECT_EXCHANGE)
    public Exchange directExchange(){
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE).durable(true).build();
    }

    /**
     * 取消订单 消息队列
     * @return
     */
    @Bean(CANCEL_ORDER)
    public Queue cancelOrderQueue(){
        return new Queue(CANCEL_ORDER,true,false,true);
    }

    /**
     * 把取消订单消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding cancelOrderBinding(@Qualifier(CANCEL_ORDER) Queue queue,
                                      @Qualifier(DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :CANCEL_ORDER,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange).with(CANCEL_ORDER).noargs();
    }

    /**
     * 自动确认订单 消息队列
     * @return
     */
    @Bean(CONFIRM_ORDER)
    public Queue confirmOrderQueue(){
        return new Queue(CONFIRM_ORDER,true,false,true);
    }

    /**
     * 把自动确认订单消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding confirmOrderBinding(@Qualifier(CONFIRM_ORDER) Queue queue,
                                       @Qualifier(DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :CANCEL_ORDER,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange).with(CONFIRM_ORDER).noargs();
    }
}
