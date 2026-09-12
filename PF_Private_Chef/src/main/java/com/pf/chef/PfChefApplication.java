package com.pf.chef;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PF_Private_Chef —— 上门私厨小程序后端
 *
 * 启动：mvn spring-boot:run
 * 默认端口 8080，接口前缀 /api
 */
@SpringBootApplication
@MapperScan("com.pf.chef.mapper")
public class PfChefApplication {

    public static void main(String[] args) {
        SpringApplication.run(PfChefApplication.class, args);
    }
}
