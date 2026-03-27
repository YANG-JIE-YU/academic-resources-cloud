package cn.academic.cloud.common.util;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * JWT utility methods.
 */
public final class JwtUtils {

    private JwtUtils() {
    }

    public static String createToken(Long userId, String username, String role, String secret) {
        return createToken(userId, username, role, secret, CommonConstants.DEFAULT_TOKEN_EXPIRE_SECONDS);
    }

    public static String createToken(Long userId, String username, String role, String secret, long expireSeconds) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expireSeconds * 1000L);

        return Jwts.builder()
                .claim(CommonConstants.CLAIM_USER_ID, userId)
                .claim(CommonConstants.CLAIM_USERNAME, username)
                .claim(CommonConstants.CLAIM_ROLE, role)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(buildSecretKey(secret))
                .compact();
    }

    public static Claims parseClaims(String token, String secret) {
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("JWT token is empty");
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(buildSecretKey(secret))
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (JwtException ex) {
            throw new BusinessException(CommonConstants.ERROR_CODE, "JWT token is invalid or expired", ex);
        }
    }

    public static boolean isExpired(Claims claims) {
        return claims.getExpiration() != null && claims.getExpiration().before(new Date());
    }

    public static String extractToken(String authorizationHeader) {
        if (StringUtils.isBlank(authorizationHeader)) {
            return null;
        }
        String value = authorizationHeader.trim();
        if (value.startsWith(CommonConstants.TOKEN_PREFIX)) {
            return value.substring(CommonConstants.TOKEN_PREFIX.length()).trim();
        }
        return value;
    }

    public static Long getUserId(Claims claims) {
        Object value = claims.get(CommonConstants.CLAIM_USER_ID);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    public static String getUsername(Claims claims) {
        Object value = claims.get(CommonConstants.CLAIM_USERNAME);
        return value == null ? null : String.valueOf(value);
    }

    public static String getRole(Claims claims) {
        Object value = claims.get(CommonConstants.CLAIM_ROLE);
        return value == null ? null : String.valueOf(value);
    }

    private static SecretKey buildSecretKey(String secret) {
        if (StringUtils.isBlank(secret)) {
            throw new BusinessException("JWT secret must not be blank");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = sha256(keyBytes);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] sha256(byte[] source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(source);
        } catch (NoSuchAlgorithmException ex) {
            throw new BusinessException(CommonConstants.ERROR_CODE, "Unable to initialize SHA-256", ex);
        }
    }
}
