package org.eu.mall.seckill.controller;

import org.eu.common.utils.R;
import org.eu.mall.seckill.service.SeckillService;
import org.eu.mall.seckill.to.SecKillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
@Controller
public class SecKillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R currentSeckillSkus() {
        List<SecKillSkuRedisTo> currentSeckillSkus = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(currentSeckillSkus);
    }

    /**
     * 查询商品是否参加秒杀活动 参与则返回场次信息和sku信息
     *
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
        SecKillSkuRedisTo skuRedisTo = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(skuRedisTo);
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId, @RequestParam("key") String key,
                     @RequestParam("num") Integer num, Model model) {
        String orderSn = seckillService.kill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}
