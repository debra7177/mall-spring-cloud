package org.eu.mall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单确认需要用的数据
 */
@Data
public class OrderConfirmVo {
    // 收货地址 ums_member_receive_address表
    @Setter
    @Getter
    List<MemberAddressVo> address;
    // 所有选中的购物项
    @Getter
    @Setter
    List<OrderItemVo> items;

    //发票记录....

    //优惠券信息...
    @Setter
    @Getter
    Integer integration;

    //库存信息
    @Setter
    @Getter
    Map<Long,Boolean> stocks;

    //防重令牌
    @Setter
    @Getter
    String orderToken;

    public Integer getCount() {
        return items.stream().mapToInt(OrderItemVo::getCount).sum();
    }
    //    BigDecimal total;//订单总额
    public BigDecimal getTotal() {
        return items.stream().map(item -> {
            BigDecimal realPrice = item.getPrice();
            return realPrice.multiply(new BigDecimal(item.getCount().toString()));
        }).reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    //应付价格
    //    BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return  getTotal();
    }
}
