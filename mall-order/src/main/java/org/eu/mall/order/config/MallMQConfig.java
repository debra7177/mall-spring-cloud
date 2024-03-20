package org.eu.mall.order.config;

import com.rabbitmq.client.Channel;
import org.eu.mall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// 实现设置队列过期时间的延时队列
@Configuration
public class MallMQConfig {
    ///**
    // * 测试收到延迟队列消息
    // * @param entity
    // */
    //@RabbitListener(queues = "order.release.order.queue")
    //public void liistener(OrderEntity entity, Channel channel, Message message) throws IOException {
    //    System.out.println("收到过期订单信息,准备关闭订单" + entity.getOrderSn());
    //    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    //
    //}
    // Binding，Queue，Exchange

    /**
     * 创建死信队列 ![](https://pic.vbean.eu.org/images/2024/03/1028d25862be6b18eef53c0599cd205c.png)
     * 容器中的 Binding，Queue，Exchange 都会自动创建（RabbitMQ没有的情况）
     * RabbitMQ 只要有。@Bean声明属性发生变化也不会覆盖 需要删除queue 或者 exchange再重启
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        /**
         * x-dead-letter-exchange: order-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000
         */
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);
        //String name 名称, boolean durable队列是否可持久化，默认为true, boolean exclusive 是否具有排它性，默认为false,
        // boolean autoDelete队列没有任何订阅的消费者时是否自动删除，默认为false,
        // Map<String, Object> arguments 一个Map集合，是AMQP协议留给AMQP实现做扩展使用的
        //a . 消息剩余生存时间【x-message-ttl】
        //b . 队列自动过期时间【x-expires】
        //c . 队列最大消息数目【x-max-length】
        //d . 队列存储所有消息的最大存储【x-max-length-bytes】
        //e . 消息优先级【x-max-priority】
        //f . 死亡交换机【Dead letter exchange】 和死亡路由键【 Dead letter routing key】
        //g . 延迟模式Lazy mode【x-queue-mode=lazy】
        //h . 主定位器Master locator【x-queue-master-locator】
        /**
         * @link https://blog.csdn.net/qq_29229567/article/details/86524915
         */
        return new Queue("order.delay.queue", true, false, false, arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order.event.exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }
    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }
    /**
     * 订单释放直接和库存释放进行绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    /**
     * 秒杀订单队列
     * @return
     */
    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    /**
     * 绑定关系
     * @return
     */
    @Bean
    public Binding orderSeckillOrderQueueBinding(){
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey,
         * 			Map<String, Object> arguments
         */
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }
}
