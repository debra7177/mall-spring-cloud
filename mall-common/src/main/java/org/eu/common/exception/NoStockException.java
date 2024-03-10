package org.eu.common.exception;

public class NoStockException extends RuntimeException {
    public NoStockException(Long skuId) {
        super("商品库存不足，skuId：" + skuId);
    }
    public NoStockException(String msg) {
        super(msg);
    }
}
