package com.kevin.basecore.modules.security.filter;

import com.kevin.basecore.modules.security.SecurityConstants;
import com.kevin.basecore.modules.security.model.LoginUser;
import com.kevin.basecore.modules.security.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        
        log.debug("请求头 Authorization: {}", header);
        
        if (StringUtils.hasText(header) && header.toLowerCase().startsWith(SecurityConstants.TOKEN_PREFIX.toLowerCase())) {
            String token = header.substring(SecurityConstants.TOKEN_PREFIX.length()).trim();
            log.debug("提取的 Token: {}", token);
            
            if (!StringUtils.hasText(token)) {
                log.debug("Token 为空，跳过认证");
                chain.doFilter(request, response);
                return;
            }

            try {
                if (token.split("\\.").length != 3) {
                    log.warn("Token 格式无效: {}", token);
                    chain.doFilter(request, response);
                    return;
                }

                String userName = jwtUtil.getUsernameFromToken(token);
                log.debug("Token 中的用户名: {}", userName);

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String redisKey = SecurityConstants.ONLINE_PRE + userName;
                    LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(redisKey);
                    log.debug("Redis 中的用户信息: {}", loginUser);
                    log.debug("Redis Key: {}", redisKey);

                    if (loginUser != null && jwtUtil.validateToken(token, userName)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                loginUser, null, loginUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("认证成功，用户: {}", userName);
                    } else {
                        if (loginUser == null) {
                            log.warn("Redis 中未找到用户信息: {}", userName);
                        } else {
                            log.warn("Token 验证失败: {}", userName);
                        }
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("JWT token 已过期");
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                log.warn("JWT token 格式错误: {}", e.getMessage());
            } catch (io.jsonwebtoken.SignatureException e) {
                log.warn("JWT 签名无效: {}", e.getMessage());
            } catch (Exception e) {
                log.error("认证失败", e);
            }
        } else {
            log.debug("请求头中没有 Authorization 或格式不正确");
        }

        chain.doFilter(request, response);
    }
}
