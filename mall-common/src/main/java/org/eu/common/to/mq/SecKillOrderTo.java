package org.eu.common.to.mq;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀订单信息
 */
@Data
public class SecKillOrderTo {
    private String orderSn; //订单号
    private Long promotionSessionId;  //活动场次id
    private Long skuId;  //商品id
    private BigDecimal seckillPrice; //秒杀价格
    private Integer num; //购买数量
    private Long memberId;//会员id；
}
