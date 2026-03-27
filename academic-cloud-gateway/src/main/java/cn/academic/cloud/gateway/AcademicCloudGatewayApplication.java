package cn.academic.cloud.gateway;

import cn.academic.cloud.gateway.config.GatewaySecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GatewaySecurityProperties.class)
public class AcademicCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicCloudGatewayApplication.class, args);
    }
}
