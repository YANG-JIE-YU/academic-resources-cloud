package cn.academic.cloud.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "academic.jwt")
public class UserJwtProperties {

    private String secret = "replace-with-a-strong-secret-at-least-32-bytes";
    private long expireSeconds = 24 * 60 * 60L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
