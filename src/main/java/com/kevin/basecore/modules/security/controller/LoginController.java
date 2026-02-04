package com.kevin.basecore.modules.security.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.security.SecurityConstants;
import com.kevin.basecore.modules.security.model.LoginVO;
import com.kevin.basecore.modules.security.service.LoginServiceImpl;
import com.kevin.intellieventback.domin.dto.UserLoginDTO;
import com.wf.captcha.SpecCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Tag(name = "认证接口")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginServiceImpl loginService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody UserLoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout.do")
    public Result<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            loginService.logout(authentication.getName());
        }
        return Result.success();
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();
        
        // 存入 Redis，有效期 2 分钟
        redisTemplate.opsForValue().set(SecurityConstants.CAPTCHA_PRE + key, verCode, 2, TimeUnit.MINUTES);
        
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", key);
        result.put("img", specCaptcha.toBase64());
        
        return Result.success(result);
    }
}
