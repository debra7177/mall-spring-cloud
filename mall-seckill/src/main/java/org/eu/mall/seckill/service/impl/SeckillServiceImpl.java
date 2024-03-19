package org.eu.mall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eu.common.utils.R;
import org.eu.mall.seckill.feign.CouponFeignService;
import org.eu.mall.seckill.feign.ProductFeignService;
import org.eu.mall.seckill.service.SeckillService;
import org.eu.mall.seckill.to.SecKillSkuRedisTo;
import org.eu.mall.seckill.vo.SeckillSesssionsWithSkus;
import org.eu.mall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码
    @Override
    public void uploadSeckillSkuLatest3Days() {
        //扫描最近三天需要参与秒杀的活动
        R latest3DaySession = couponFeignService.getLatest3DaySession();
        if (latest3DaySession.getCode() == 0) {
            // 上架商品
            List<SeckillSesssionsWithSkus> sessionData = latest3DaySession.getData(new TypeReference<List<SeckillSesssionsWithSkus>>() {
            });
            // 缓存到redis
            // 缓存活动信息
            saveSessionInfos(sessionData);
            //缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);
        }
    }

    /**
     * 缓存活动的关联商品信息
     * @param sessionData
     */
    private void saveSessionSkuInfos(List<SeckillSesssionsWithSkus> sessionData) {
        if (sessionData == null || sessionData.size() == 0) {
            return;
        }
        sessionData.stream().forEach(session -> {
            BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                // 随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                // 如果当前这个场次的商品的库存信息已经上架就不需要上架
                if (!Boolean.TRUE.equals(ops.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString()))) {
                    // 缓存商品
                    SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                    // sku的基本数据
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(info);
                    }
                    // sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);
                    // 设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());
                    redisTo.setRandomCode(token);
                    String jsonString = JSON.toJSONString(redisTo);
                    // 每个商品的过期时间不一样。所以，我们在获取当前商品秒杀信息的时候，做主动删除，代码在 getSkuSeckillInfo 方法里面
                    ops.put(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId().toString(), jsonString);

                    //使用库存作为分布式的信号量  限流；
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 秒杀商品的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                    // 设置过期时间
                    semaphore.expireAt(session.getEndTime());
                }
            });
        });
    }

    /**
     * 缓存活动信息
     * @param sessionData
     */
    private void saveSessionInfos(List<SeckillSesssionsWithSkus> sessionData) {
        if (sessionData == null || sessionData.size() == 0) {
            return;
        }
        sessionData.stream().forEach(session -> {
            long start = session.getStartTime().getTime();
            long end = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + start + "_" + end;
            if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                // 缓存活动信息
                stringRedisTemplate.opsForList().leftPushAll(key, collect);
                // 设置过期时间
                stringRedisTemplate.expireAt(key, new Date(end));
            }
        });
    }

    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        // 确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            //seckill:sessions:1582250400000_1582254000000
            String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {
                // 获取这个秒杀场次需要的所有商品信息
                List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);
                if (list != null) {
                    return list.stream().map(item -> {
                        SecKillSkuRedisTo redis = JSON.parseObject(item, SecKillSkuRedisTo.class);
                        //redis.setRandomCode(null); //当前秒杀开始就需要随机码
                        return redis;
                    }).collect(Collectors.toList());
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        // 找到所有需要参与秒杀的商品
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //6_4 场次_skuId
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String json = hashOps.get(key);
                    if (StringUtils.isNotEmpty(json)) {
                        SecKillSkuRedisTo skuRedisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                        if (skuRedisTo == null) {
                            return null;
                        }
                        // 随机码
                        long current = new Date().getTime();
                        if (current >= skuRedisTo.getStartTime() && current <= skuRedisTo.getEndTime()) {
                            //return skuRedisTo;
                        }else {
                            hashOps.delete(key);
                            skuRedisTo.setRandomCode(null);
                        }
                        return skuRedisTo;
                    }
                }
            }
        }
        return null;
    }
}
