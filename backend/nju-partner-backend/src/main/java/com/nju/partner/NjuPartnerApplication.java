package com.nju.partner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nju.partner.mapper")
public class NjuPartnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NjuPartnerApplication.class, args);
    }
}
