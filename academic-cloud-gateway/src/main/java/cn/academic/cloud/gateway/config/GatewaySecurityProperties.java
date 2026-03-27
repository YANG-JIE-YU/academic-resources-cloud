package cn.academic.cloud.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "academic.gateway")
public class GatewaySecurityProperties {

    /**
     * JWT secret key for gateway token verify.
     */
    private String jwtSecret = "replace-with-a-strong-secret-at-least-32-bytes";

    /**
     * Paths that do not require token authentication.
     */
    private List<String> ignoreUrls = new ArrayList<>(List.of(
            "/api/user/auth/login",
            "/api/user/auth/register",
            "/api/user/auth/logout",
            "/actuator/**"
    ));

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }
}
