package org.eu.mall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.eu.common.to.mq.SecKillOrderTo;
import org.eu.mall.order.entity.OrderEntity;
import org.eu.mall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSecKillListener {
    @Autowired
    private OrderService orderService;

    /**
     * 秒杀(延时队列实现)
     * @param secKillOrderTo
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listener(SecKillOrderTo secKillOrderTo, Channel channel, Message message) throws IOException {
        try {
            log.info("创建秒杀订单的信息");
            orderService.createSecKillOrder(secKillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
