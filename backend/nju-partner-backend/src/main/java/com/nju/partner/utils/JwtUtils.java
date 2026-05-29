package com.nju.partner.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // milliseconds

    // Simple self-contained token format (not JJWT) to avoid dependency issues in classpath.
    // token = Base64URL( userId + ":" + expiry + ":" + HMAC_SHA256(secret, userId:expiry) )

    public String generateToken(Long userId) {
        long now = System.currentTimeMillis();
        long exp = now + expiration;
        String payload = userId + ":" + exp;
        String sig = hmacSha256(payload, secret);
        String token = payload + ":" + sig;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    public Long getUserIdFromToken(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length < 3) return null;
            String userIdStr = parts[0];
            String expStr = parts[1];
            String sig = parts[2];
            String payload = userIdStr + ":" + expStr;
            String expectedSig = hmacSha256(payload, secret);
            if (!constantTimeEquals(sig, expectedSig)) return null;
            long exp = Long.parseLong(expStr);
            if (System.currentTimeMillis() > exp) return null;
            return Long.valueOf(userIdStr);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        return getUserIdFromToken(token) != null;
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(spec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC", e);
        }
    }

    // Constant-time comparison to mitigate timing attacks
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        byte[] aa = a.getBytes(StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(StandardCharsets.UTF_8);
        if (aa.length != bb.length) return false;
        int result = 0;
        for (int i = 0; i < aa.length; i++) {
            result |= aa[i] ^ bb[i];
        }
        return result == 0;
    }
}

