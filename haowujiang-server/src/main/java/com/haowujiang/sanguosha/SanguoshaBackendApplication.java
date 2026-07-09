package com.haowujiang.sanguosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.haowujiang.sanguosha.infrastructure.persistence.mapper")
@SpringBootApplication
public class SanguoshaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanguoshaBackendApplication.class, args);
    }
}


