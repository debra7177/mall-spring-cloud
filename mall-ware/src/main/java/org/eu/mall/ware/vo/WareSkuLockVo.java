package org.eu.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 锁定库存vo
 */
@Data
public class WareSkuLockVo {

    private String orderSn;//订单号

    private List<OrderItemVo> locks;//需要锁住的所有库存信息
}
