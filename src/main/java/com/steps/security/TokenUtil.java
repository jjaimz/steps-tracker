package com.steps.security;

import com.steps.business.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static com.steps.data.HikariUtil.getUserByEmail;

public class TokenUtil {

    private static final String SECRET_KEY = "ARm0et0SeyARTdnz6ZPtVEq5TxUjB2";

    public static String generateToken(String email) {
        String header = "{\"typ\":\"JWT\",\"alg\":\"HS256\"}";

        long now = System.currentTimeMillis();
        User user = getUserByEmail(email);
        assert user != null;

        String payload = "{\"sub\":\"" + user.getId() + "\",\"exp\":" + (now + 3600000) + "\",\"admin\":" + user.getAdmin() + "}";

        String b64header = Base64.getUrlEncoder().encodeToString(header.getBytes());
        String b64payload = Base64.getUrlEncoder().encodeToString(payload.getBytes());

        String signData = b64header + "." + b64payload;

        byte[] signedBytes = calculateHmacSha256(signData.getBytes(), SECRET_KEY.getBytes());

        return b64header + "." + b64payload + "." + Base64.getUrlEncoder().encodeToString(signedBytes);
    }

    public static boolean verifyToken(String token) {
        String[] parts = token.split("\\.");
        String clientSignData = parts[0] + "." + parts[1];
        byte[] signedBytes = calculateHmacSha256(clientSignData.getBytes(), SECRET_KEY.getBytes());
        return parts[2].equals(Base64.getUrlEncoder().encodeToString(signedBytes));

    }

    private static byte[] calculateHmacSha256(byte[] data, byte[] key) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            hmacSha256.init(secretKey);
            return hmacSha256.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }
}
