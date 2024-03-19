package org.eu.mall.seckill.service;

public interface SeckillService {
    /**
     * 上架最近三天的秒杀商品
     */
    void uploadSeckillSkuLatest3Days();
}
