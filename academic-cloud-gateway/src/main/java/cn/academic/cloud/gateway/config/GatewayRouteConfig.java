package cn.academic.cloud.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-route", r -> r.path("/api/user/**")
                        .uri("lb://academic-cloud-user-service"))
                .route("paper-service-route", r -> r.path("/api/paper/**")
                        .uri("lb://academic-cloud-paper-service"))
                .route("ai-service-route", r -> r.path("/api/ai/**")
                        .uri("lb://academic-cloud-ai-service"))
                .build();
    }
}
