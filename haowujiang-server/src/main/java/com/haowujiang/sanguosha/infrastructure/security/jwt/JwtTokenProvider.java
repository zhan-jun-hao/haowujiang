package com.haowujiang.sanguosha.infrastructure.security.jwt;

import cn.hutool.core.util.IdUtil;
import com.haowujiang.sanguosha.infrastructure.security.context.HeaderAuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:haowujiang-dev-secret-change-me}")
    private String secret;

    @Value("${app.jwt.expire-minutes:120}")
    private long expireMinutes;

    public String createToken(Long userId, Integer role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireMinutes * 60 * 1000);
        String traceId = IdUtil.randomUUID().replace("-", "");

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("role", role)
                .claim("traceId", traceId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    public HeaderAuthenticatedUser parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get("userId", Long.class);
            Integer role = claims.get("role", Integer.class);
            if (userId == null || role == null) {
                return null;
            }
            String traceId = claims.get("traceId", String.class);
            return new HeaderAuthenticatedUser(userId, role, traceId);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // JJWT 要求 HS256 密钥至少 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
