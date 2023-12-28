package org.eu.mall.product.web;

import org.eu.mall.product.service.SkuInfoService;
import org.eu.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据 skuId 获取商品信息
     *
     * @param skuId 商品规格的唯一标识
     * @param model 模型对象
     * @return 商品详情页面
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo item = skuInfoService.item(skuId);
        model.addAttribute("item", item);
        return "item";
    }

}
