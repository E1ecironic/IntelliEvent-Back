package com.kevin.basecore.modules.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;
import com.kevin.basecore.modules.system.service.SysConfigService;

import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private SysConfigService sysConfigService;

    @Value("${security.jwt.key-path:keys/intellievent.jks}")
    private String keyPath;

    @Value("${security.jwt.key-alias:intellievent}")
    private String keyAlias;

    @Value("${security.jwt.key-pass:123456}")
    private String keyPass;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(keyPath);
            if (resource.exists()) {
                KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keyPass.toCharArray());
                KeyPair keyPair = keyStoreKeyFactory.getKeyPair(keyAlias);
                this.privateKey = keyPair.getPrivate();
                this.publicKey = keyPair.getPublic();
                log.info("JWT RSA keys loaded successfully from {}.", keyPath);
            } else {
                log.warn("JWT RSA key file not found at {}. RSA signing will not work.", keyPath);
            }
        } catch (Exception e) {
            log.error("Failed to load JWT RSA keys from {}", keyPath, e);
        }
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        long expiration = Long.parseLong(sysConfigService.getValue("security.jwt.expiration", "1440"));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiration))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public Boolean validateToken(String token, String userName) {
        final String tokenUserName = getUsernameFromToken(token);
        return (tokenUserName.equals(userName) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
