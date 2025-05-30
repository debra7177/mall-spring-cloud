package org.eu.mall.order.web;

import org.eu.mall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

@Controller
public class PageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试 创建订单 发送消息
     * @return
     */
    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrderTest(){
        //订单下单成功
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(UUID.randomUUID().toString());
        entity.setModifyTime(new Date());

        //给MQ发送消息。
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",entity);
        return "ok";
    }
    @GetMapping("/{page.html}")
    public String listPage(@PathVariable("page") String page) {
        return page;
    }
}
