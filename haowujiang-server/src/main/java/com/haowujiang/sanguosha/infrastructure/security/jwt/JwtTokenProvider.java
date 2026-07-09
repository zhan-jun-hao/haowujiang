package com.haowujiang.sanguosha.infrastructure.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haowujiang.sanguosha.infrastructure.security.context.HeaderAuthenticatedUser;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper;

    @Value("${app.jwt.secret:haowujiang-dev-secret-change-me}")
    private String secret;

    @Value("${app.jwt.expire-minutes:120}")
    private long expireMinutes;

    public String createToken(Long userId, Integer role) {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", String.valueOf(userId));
        payload.put("userId", userId);
        payload.put("role", role);
        payload.put("iat", now);
        payload.put("exp", now + expireMinutes * 60);

        String unsignedToken = base64Url(toJson(header)) + "." + base64Url(toJson(payload));
        return unsignedToken + "." + sign(unsignedToken);
    }

    public HeaderAuthenticatedUser parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        String unsignedToken = parts[0] + "." + parts[1];
        if (!sign(unsignedToken).equals(parts[2])) {
            return null;
        }
        Map<String, Object> payload = fromJson(new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8));
        long exp = numberValue(payload.get("exp")).longValue();
        if (Instant.now().getEpochSecond() > exp) {
            return null;
        }
        Long userId = numberValue(payload.get("userId")).longValue();
        Integer role = numberValue(payload.get("role")).intValue();
        return new HeaderAuthenticatedUser(userId, role);
    }

    private String toJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("JWT 序列化失败", exception);
        }
    }

    private Map<String, Object> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private Number numberValue(Object value) {
        if (value instanceof Number number) {
            return number;
        }
        return 0;
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT 签名失败", exception);
        }
    }
}
