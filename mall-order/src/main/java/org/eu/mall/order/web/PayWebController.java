package org.eu.mall.order.web;

import com.alipay.api.AlipayApiException;
import org.eu.mall.order.config.AlipayTemplate;
import org.eu.mall.order.service.OrderService;
import org.eu.mall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {
    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        //PayVo payVo = new PayVo();
        //payVo.setBody();//订单的备注
        //payVo.setOut_trade_no();//订单号
        //payVo.setSubject();//订单的主题
        //payVo.setTotal_amount();
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }
}
