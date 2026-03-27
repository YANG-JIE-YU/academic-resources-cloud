package cn.academic.cloud.gateway.filter;

import cn.academic.cloud.common.constant.CommonConstants;
import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.common.util.JwtUtils;
import cn.academic.cloud.gateway.config.GatewaySecurityProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLE = "X-Role";

    private final GatewaySecurityProperties securityProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public TokenAuthenticationFilter(GatewaySecurityProperties securityProperties, ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest sanitizedRequest = sanitizeInternalHeaders(request);
        String path = sanitizedRequest.getURI().getPath();
        String authorization = sanitizedRequest.getHeaders().getFirst(CommonConstants.HEADER_AUTHORIZATION);
        String token = JwtUtils.extractToken(authorization);

        if (HttpMethod.OPTIONS.equals(sanitizedRequest.getMethod())) {
            return chain.filter(exchange.mutate().request(sanitizedRequest).build());
        }
        if (isIgnorePath(path)) {
            if (!StringUtils.hasText(token)) {
                return chain.filter(exchange.mutate().request(sanitizedRequest).build());
            }
            try {
                Claims claims = JwtUtils.parseClaims(token, securityProperties.getJwtSecret());
                if (JwtUtils.isExpired(claims)) {
                    return writeUnauthorized(exchange.getResponse(), "unauthorized: token expired");
                }
                ServerHttpRequest mutateRequest = injectUserHeaders(sanitizedRequest, claims);
                return chain.filter(exchange.mutate().request(mutateRequest).build());
            } catch (Exception ex) {
                return writeUnauthorized(exchange.getResponse(), "unauthorized: invalid token");
            }
        }

        if (!StringUtils.hasText(token)) {
            return writeUnauthorized(exchange.getResponse(), "unauthorized: missing token");
        }

        try {
            Claims claims = JwtUtils.parseClaims(token, securityProperties.getJwtSecret());
            if (JwtUtils.isExpired(claims)) {
                return writeUnauthorized(exchange.getResponse(), "unauthorized: token expired");
            }
            ServerHttpRequest mutateRequest = injectUserHeaders(sanitizedRequest, claims);
            return chain.filter(exchange.mutate().request(mutateRequest).build());
        } catch (Exception ex) {
            return writeUnauthorized(exchange.getResponse(), "unauthorized: invalid token");
        }
    }

    private ServerHttpRequest sanitizeInternalHeaders(ServerHttpRequest request) {
        return request.mutate()
                .headers(headers -> {
                    headers.remove(HEADER_USER_ID);
                    headers.remove(HEADER_USERNAME);
                    headers.remove(HEADER_ROLE);
                })
                .build();
    }

    private boolean isIgnorePath(String path) {
        if (pathMatcher.match("/**/admin/**", path) || pathMatcher.match("/**/internal/**", path)) {
            return false;
        }
        List<String> ignoreUrls = securityProperties.getIgnoreUrls();
        if (ignoreUrls == null || ignoreUrls.isEmpty()) {
            return false;
        }
        return ignoreUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private ServerHttpRequest injectUserHeaders(ServerHttpRequest request, Claims claims) {
        Long claimUserId = JwtUtils.getUserId(claims);
        String userId = claimUserId == null ? null : String.valueOf(claimUserId);
        String username = JwtUtils.getUsername(claims);
        String role = JwtUtils.getRole(claims);

        ServerHttpRequest.Builder builder = request.mutate();
        if (StringUtils.hasText(userId)) {
            builder.header(HEADER_USER_ID, userId);
        }
        if (StringUtils.hasText(username)) {
            builder.header(HEADER_USERNAME, username);
        }
        if (StringUtils.hasText(role)) {
            builder.header(HEADER_ROLE, role);
        }
        return builder.build();
    }

    private Mono<Void> writeUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<Void> result = Result.fail(HttpStatus.UNAUTHORIZED.value(), message);
        byte[] bytes = toJsonBytes(result);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private byte[] toJsonBytes(Object data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException ex) {
            return "{\"code\":401,\"msg\":\"Unauthorized\"}".getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
