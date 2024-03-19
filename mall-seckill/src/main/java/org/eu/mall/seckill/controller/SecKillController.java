package org.eu.mall.seckill.controller;

import org.eu.common.utils.R;
import org.eu.mall.seckill.service.SeckillService;
import org.eu.mall.seckill.to.SecKillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
@Controller
public class SecKillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R currentSeckillSkus() {
        List<SecKillSkuRedisTo> currentSeckillSkus = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(currentSeckillSkus);
    }
}
