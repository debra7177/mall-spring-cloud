package org.eu.mall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    // 总件数
    private Integer countNum;
    //商品类型数量
    private Integer countType;
    //总价格
    private BigDecimal totalAmount;
    // 优惠价格
    private BigDecimal reduce = new BigDecimal("0");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                amount = amount.add(item.getTotalPrice());
            }
        }
        // 减去优惠
        amount = amount.subtract(reduce);
        return amount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
