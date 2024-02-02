package org.eu.mall.auth.controller;

import org.apache.commons.lang.StringUtils;
import org.eu.common.constant.AuthConstant;
import org.eu.common.exception.BizCodeEnum;
import org.eu.common.utils.R;
import org.eu.mall.auth.feign.ThirdPartFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        String redisCode = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                // 60秒内不能再次发送
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        // 验证码的再次校验
        String code = String.format("%06d", new BigInteger(UUID.randomUUID().toString().substring(0, 5), 16));
        String substring = code + "_" + System.currentTimeMillis();

        stringRedisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + phone, substring, 10, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    @GetMapping("/login.html")
    public String login() {
        return "login";
    }

    @GetMapping("/reg.html")
    public String reg() {
        return "reg";
    }
}
