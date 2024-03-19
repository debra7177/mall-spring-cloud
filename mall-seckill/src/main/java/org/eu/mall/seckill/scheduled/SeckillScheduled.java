package org.eu.mall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.eu.mall.seckill.service.SeckillService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品的定时上架；
 * 每天晚上3点；上架最近三天需要秒杀的商品。
 * 当天00:00:00  - 23:59:59
 * 明天00:00:00  - 23:59:59
 * 后天00:00:00  - 23:59:59
 */
@Slf4j
@Service
public class SeckillScheduled {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;

    private final String UPLOAD_LOCK = "seckill:upload:lock";

    //每分钟执行一次吧，上线后调整为每天晚上3点执行
    @Scheduled(cron = "0 * * * * ?")
    //@Scheduled(cron = "0 0 3 * * ?") //线上模式
    public void updateSeckillSkuLatest3Days() {
        log.info("上架秒杀商品信息...");
        // 分布式锁。锁的业务执行完成，状态已经更新完成。释放锁以后。其他人获取到就会拿到最新的状态。
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
        log.info("上架秒杀商品信息完成...");
    }
}
