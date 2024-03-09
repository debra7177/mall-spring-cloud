package org.eu.mall.order.to;


import lombok.Data;
import org.eu.mall.order.entity.OrderEntity;
import org.eu.mall.order.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

// 创建的订单信息
@Data
public class OrderCreateTo {


    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;//订单计算的应付价格

    private BigDecimal fare;//运费
}
