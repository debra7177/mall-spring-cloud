package org.eu.mall.order.config;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import lombok.Data;
import org.eu.mall.order.vo.PayVo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "9021000129689782";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCsSXmPCik9wgRkNGAHvvOrUIlpOchMUlgYzI3L/ZmpxA20sgJtW679S/bzdXuU5lpo4ibBVp4Ohz2NMZIW4Iy0rSnt20bLKO4udDnhO8YsvCM4G015Oqb4r0vNV6238SKuIrdnHt+R2wW4p/fvkFzeBifPvxAQKxbXEKr9EYe4CT9isjqQ11KgexLL6bpiSlf/FfKeewE2wP5cfHGhDph9w7yDNmg48iiBpd5dI0qvbj0JkQ+x+BuA7egdJr0/8ElSEA20A0lcDivtFtxbvG/JI+klvD7bL2X8pdJfTqMeNmmranHmKQ2jMLjdkdJad/r3cdiJ8gAV71zJzJm02ikZAgMBAAECggEBAJJPB0sg3usSY7ZRACgz0ixvBXJ13pJp72SH+fb9E0ynMNh9vy/th0qWA063O0603JxrSWwwyaMe2HeaXCzPXs5kq9HRwGnxBWwvyaNrpp0A3IveK8nCtF9GRoJIbj9iKQhmdQe+oS/1JwOsqRIpr6Pg6Fg1Mkf4v7m97udgxrfSwvKpbn+qTU0zqDO1XhKr9yqbIshnncO7UzvqkA1v3XdKPdMtfAJnLR0t0/gT2XX2JVFFQEPKTi1V9fjPrplHTlI/4lXNk45XEmh1Xyy3/zXywUhrXTaq8+khH8PHCMlt/o8GSetqWuOQt6EWpLDDHbWJgoxdpfsu/dp0A6SbCoECgYEA7vmpRMQFAiZ1AYa9ww84hwtW4aWA9Dy1Uyc7Mqw6+yQpe0f2WRYQCmFmQw6FC68CT2LPrX0ymRdbrS+vz2faRwD3ODRH23oXMrIBJaKAfA4iZBpXu1QnBQaBOxOYo1m+DtLe1puFTnRoasicZfkbKuZsWVLCAV+pLYfXZIJftSkCgYEAuI+UTNYfiQ+HtJZ72ewvuq/HYFRw97lmacLtuTfYePJd4w753tR9FNi7R6f7tfwTnbqBEmLeJVsti2NEJCPRJKf1QuRclJS1ckokD+dG7Wypsf9JpMUUBc/o1f7gmqhpngVR9mxqGeP4E8kK+pgStClLHl0MGMeXp85oYFXl4nECgYBmwYt7Oh49MnQVV+64tQ7FYqbVF+k/G+uL+c6Gl3A1FPORQvw2ijoTfJYQN2oWAAaqQ/6wxqi/E0AkU+Dw+eLtrQigbjqHsRPQhKHhoOJGdX1sNbeLoT+19XoUAPUdKDWFAj83YGANwBg18lTttBNueu4T6nXpSUzhewuu+DBXIQKBgEbTYrfx2/w/J3bX4QOKLe7YkgyXmxqV3YVstsnDcqFHcVw3XRB0PuQHICepgWEfMW/wKKQjqNhxGxNIrtennxUQF4DqHdUvFEqdxSUNPhzS/HRAYcSHlN1+If4tZ4gyZQWtSPzmMflpkMQW/M2aGmNlDVzDnGbOAZb2EPwXOrchAoGBAKuLo6WDo/mFhMhPfOwBCSHoCxqWBPuOqMxxMKruzmsGvBx50BI4nE2RU1gcEOQ92pqxhtoTUFrQzXP7Ca1T3pi7vdGtIazsbwurEXYPk1HaQsyr6Cys5T2rvlww/MMZioaB4FJhXOET+Qln4iHQ/F+RppTKN1MW/FUIHINCXOwd";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhfU41N3pWiB7ewJve2tVTNelZdDAjRlDIz4CdgrPZqtH4/iacMHdHUP63AkojwxXhEgjYPV+WnjWRCWcdja7l1vA+J/5qpdeUM73hzzOywtMvHlu8a4BEpWN7d9/Lmx5xB5dKaFlmRlFEgSDH9vHPnQQYW4dMLqIJFO5rCboJ1aetuqi0CN7pW2m79Z0BmHQIWZdyUCKTEvyzd0vYDiAeEu96juYm0Em5stlXrzP0/dPiPR4y3rQvxKlIqfg7GLjrIumoEyHhszRYgeralICdaz+M4BITjE9jz8fv2CHImtX6amwV9kkzpxAHV7zVibEqR/yvGo7xL2o2lwXG1U+fwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://497n86m7k7.52http.net/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.vmake.eu.org/memberOrder.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public String pay(PayVo vo) {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();
        //HashMap<String, Object> map = new HashMap<>();
        //map.put("out_trade_no", out_trade_no);
        //map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //map.put("total_amount", total_amount);
        //map.put("subject", subject);
        //alipayRequest.setBizContent(JSON.toJSONString(map));

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = null;
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
