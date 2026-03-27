package cn.academic.cloud.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "cn.academic.cloud")
@EnableFeignClients(basePackages = "cn.academic.cloud.ai.client")
@MapperScan("cn.academic.cloud.ai.mapper")
public class AcademicCloudAiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicCloudAiServiceApplication.class, args);
    }
}
