package org.eu.mall.order.vo;

import lombok.Data;
import org.eu.mall.order.entity.OrderEntity;

@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;
    private Integer code;//0成功   错误状态码
}
