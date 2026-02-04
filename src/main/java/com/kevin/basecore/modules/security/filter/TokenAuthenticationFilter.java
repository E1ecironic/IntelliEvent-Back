package com.kevin.basecore.modules.security.filter;

import com.kevin.basecore.modules.security.SecurityConstants;
import com.kevin.basecore.modules.security.model.LoginUser;
import com.kevin.basecore.modules.security.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        
        // 改进 Token 提取逻辑，支持大小写不敏感的 Bearer 前缀
        if (StringUtils.hasText(header) && header.toLowerCase().startsWith(SecurityConstants.TOKEN_PREFIX.toLowerCase())) {
            String token = header.substring(SecurityConstants.TOKEN_PREFIX.length()).trim();
            if (!StringUtils.hasText(token)) {
                chain.doFilter(request, response);
                return;
            }

            try {
                // 增加简单的格式检查，避免解析非 JWT 格式的字符串
                if (token.split("\\.").length != 3) {
                    logger.warn("Token format invalid (no dots found): " + token);
                    chain.doFilter(request, response);
                    return;
                }

                String userName = jwtUtil.getUsernameFromToken(token);

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 校验 Redis 状态
                    LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(SecurityConstants.ONLINE_PRE + userName);

                    if (loginUser != null && jwtUtil.validateToken(token, userName)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                loginUser, null, loginUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("JWT token is expired: {}");
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.warn("JWT token is malformed: {}");
            } catch (io.jsonwebtoken.SignatureException e) {
                logger.warn("JWT signature is invalid: {}");
            } catch (Exception e) {
                logger.error("Could not set user authentication in security context", e);
            }
        }

        chain.doFilter(request, response);
    }
}
