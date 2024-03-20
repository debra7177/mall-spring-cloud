package org.eu.mall.seckill.service;

import org.eu.mall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

public interface SeckillService {
    /**
     * 上架最近三天的秒杀商品
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 返回指定商品的秒杀信息
     * @param skuId
     * @return
     */
    SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * 秒杀
     * @param killId
     * @param key
     * @param num
     * @return
     */
    String kill(String killId, String key, Integer num);
}
