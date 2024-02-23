package org.eu.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.eu.common.constant.AuthConstant;
import org.eu.common.exception.BizCodeEnum;
import org.eu.common.utils.R;
import org.eu.common.vo.MemberRespVo;
import org.eu.mall.auth.feign.MemberFeignService;
import org.eu.mall.auth.feign.ThirdPartyFeignService;
import org.eu.mall.auth.vo.UserLoginVo;
import org.eu.mall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

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
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            // 校验出错 重定向到注册页面
            return "redirect:http://auth.vmake.eu.org/reg.html";
        }
        // 校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                stringRedisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                // 调用远程服务注册
                R regist = memberFeignService.regist(vo);
                if (regist.getCode() == 0) {
                    return "redirect:http://auth.vmake.eu.org/login.html";
                } else {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg", regist.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.vmake.eu.org/reg.html";
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.vmake.eu.org/reg.html";
            }

        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.vmake.eu.org/reg.html";
        }
    }

    @GetMapping("/login.html")
    public String login(HttpSession session) {
        Object attribute = session.getAttribute(AuthConstant.LOGIN_USER);
        if (attribute == null) {
            return "login";
        } else {
            return "redirect:http://www.vmake.eu.org";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0) {
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthConstant.LOGIN_USER, data);
            return "redirect:http://www.vmake.eu.org";
        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", login.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.vmake.eu.org/login.html";
        }
    }
}
