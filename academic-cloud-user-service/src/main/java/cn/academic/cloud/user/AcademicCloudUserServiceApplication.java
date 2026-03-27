package cn.academic.cloud.user;

import cn.academic.cloud.user.config.UserJwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "cn.academic.cloud")
@EnableFeignClients(basePackages = "cn.academic.cloud")
@MapperScan("cn.academic.cloud.user.mapper")
@EnableConfigurationProperties(UserJwtProperties.class)
public class AcademicCloudUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicCloudUserServiceApplication.class, args);
    }
}
