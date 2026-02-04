package com.kevin.basecore.modules.security.service;

import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.security.SecurityConstants;
import com.kevin.basecore.modules.security.model.LoginUser;
import com.kevin.basecore.modules.security.model.LoginVO;
import com.kevin.basecore.modules.security.utils.JwtUtil;
import com.kevin.intellieventback.domin.dto.UserLoginDTO;
import com.kevin.intellieventback.domin.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @org.springframework.beans.factory.annotation.Value("${security.captcha.enabled:true}")
    private boolean captchaEnabled;

    public Result<LoginVO> login(UserLoginDTO loginDTO) {
        // 1. 校验验证码 (sa 账号免码登录)
        if (captchaEnabled && !"sa".equals(loginDTO.getUserName())) {
            String captchaKey = SecurityConstants.CAPTCHA_PRE + loginDTO.getUuid();
            String captchaCode = (String) redisTemplate.opsForValue().get(captchaKey);
            if (captchaCode == null || !captchaCode.equalsIgnoreCase(loginDTO.getCode())) {
                return Result.error("验证码错误或已过期");
            }
        }

        // 2. 身份认证
        UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 获取用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Users user = loginUser.getUser();

        // 4. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = jwtUtil.generateToken(user.getUserName(), claims);

        // 5. 存入 Redis (在线用户状态)
        redisTemplate.opsForValue().set(SecurityConstants.ONLINE_PRE + user.getUserName(), loginUser, 24, TimeUnit.HOURS);

        // 6. 构造返回对象
        LoginVO loginVO = LoginVO.builder()
                .token(token)
                .user(LoginVO.UserVO.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .realName(user.getRealName())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .build();

        return Result.success(loginVO);
    }

    public void logout(String userName) {
        redisTemplate.delete(SecurityConstants.ONLINE_PRE + userName);
    }
}
