package cn.academic.cloud.paper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.academic.cloud")
@MapperScan("cn.academic.cloud.paper.mapper")
public class AcademicCloudPaperServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicCloudPaperServiceApplication.class, args);
    }
}
