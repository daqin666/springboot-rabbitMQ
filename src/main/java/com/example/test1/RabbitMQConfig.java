package com.example.test1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    // 定义交换机的名称
    public static final String EXCHANGE_ARTICLE = "exchange_article";

    // 定义队列的名称
    public static final String QUEUE_DOWNLOAD_HTML = "queue_download_html";

    // 创建交换机，放入springboot容器
    @Bean(EXCHANGE_ARTICLE)
    public Exchange exchange() {
        return ExchangeBuilder                      // 构建交换机
                .topicExchange(EXCHANGE_ARTICLE)    // 使用topic类型，并定义交换机的名称。
                .durable(true)                      // 设置持久化，重启MQ后依然存在
                .build();
        // 四个参数：name（交换机名）、durable（持久化）、autoDelete（自动删除）、arguments（额外参数）
        // durable:是否持久化,默认是false,持久化交换机。
        // autoDelete:是否自动删除，交换机先有队列或者其他交换机绑定的时候，然后当该交换机没有队列或其他交换机绑定的时候，会自动删除。
        // arguments：交换机设置的参数，比如设置交换机的备用交换机（Alternate Exchange），当消息不能被路由到该交换机绑定的队列上时，会自动路由到备用交换机
//        return new DirectExchange(Direct_Exchange, true, false);
    }

    // 创建队列
    @Bean(QUEUE_DOWNLOAD_HTML)
    public Queue queue() {
        // 四个参数：name（队列名）、durable（持久化）、 exclusive（独占）、autoDelete（自动删除）
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，有消息者订阅本队列，然后所有消费者都解除订阅此队列，会自动删除。
        // arguments：队列携带的参数，比如设置队列的死信队列，消息的过期时间等等。
        return new Queue(QUEUE_DOWNLOAD_HTML,true);
    }

    // 队列绑定交换机
    @Bean
    public Binding binding(
            @Qualifier(QUEUE_DOWNLOAD_HTML) Queue queue,
            @Qualifier(EXCHANGE_ARTICLE) Exchange exchange) {
        return BindingBuilder               // 定义绑定关系
                .bind(queue)                // 绑定队列
                .to(exchange)               // 到交换机
                .with("article.*")   // 定义路由规则（requestMapping映射）
                .noargs();                  // 执行绑定
    }

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //设置消息投递失败的策略，有两种策略：自动删除或返回到客户端。
        //我们既然要做可靠性，当然是设置为返回到客户端(true是返回客户端，false是自动删除)
//        rabbitTemplate.setMandatory(true);

        // 设置消息确认回调方法
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * MQ确认回调方法
             * @param correlationData 消息的唯一标识
             * @param ack 消息是否成功收到
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                // 记录日志
                log.info("ConfirmCallback...correlationData["+correlationData+"]==>ack:["+ack+"]==>cause:["+cause+"]");
                if (!ack) {
                    // 出错处理

                }
            }
        });

        // 设置路由失败回调方法
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * MQ没有将消息投递给指定的队列回调方法
             * @param message 投递失败的消息详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange 消息发给哪个交换机
             * @param routingKey 消息用哪个路邮键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText,
                                        String exchange, String routingKey) {
                // 记录日志
                log.info("Fail Message["+message+"]==>replyCode["+replyCode+"]" +
                        "==>replyText["+replyText+"]==>exchange["+exchange+
                        "]==>routingKey["+routingKey+"]");
                // 出错处理

            }
        });

        return rabbitTemplate;
    }

//    @Bean
//    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);             //开启手动 ack
//        return factory;
//    }
}
