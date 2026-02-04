package com.kevin.basecore.modules.security;

/**
 * 安全相关常量
 */
public interface SecurityConstants {

    /**
     * 令牌前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌请求头
     */
    String HEADER_STRING = "Authorization";

    /**
     * 在线用户 Redis 前缀
     */
    String ONLINE_PRE = "online_user:";

    /**
     * 验证码 Redis 前缀
     */
    String CAPTCHA_PRE = "captcha:";
    
    /**
     * 令牌有效期（分钟）
     */
    long EXPIRATION_TIME = 1440; // 24小时
}
