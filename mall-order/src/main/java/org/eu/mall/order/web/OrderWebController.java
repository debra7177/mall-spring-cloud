package org.eu.mall.order.web;

import org.eu.mall.order.service.OrderService;
import org.eu.mall.order.vo.OrderConfirmVo;
import org.eu.mall.order.vo.OrderSubmitVo;
import org.eu.mall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    /**
     * 去结算确认页
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 提交订单
     * @param vo
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
        //下单失败回到订单确认页重新确认订单信息
        System.out.println("订单提交的数据..." + vo);
        if (responseVo.getCode() == 0) {
            //下单成功来到支付选择页
            model.addAttribute("submitOrderResp",responseVo);
            return  "pay";
        }else {
            String msg = "下单失败；";
            switch (responseVo.getCode()){
                case 1:
                    msg += "订单信息过期，请刷新再次提交";
                    break;
                case 2:
                    msg += "订单商品价格发生变化，请确认后再次提交";
                    break;
                case 3:
                    msg += "库存锁定失败，商品库存不足";
                    break;
            }
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.vmake.eu.org/toTrade";
        }

    }
}
