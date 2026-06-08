package com.nju.partner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.nju.partner.mapper")
@EnableScheduling
public class NjuPartnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NjuPartnerApplication.class, args);
    }
}
