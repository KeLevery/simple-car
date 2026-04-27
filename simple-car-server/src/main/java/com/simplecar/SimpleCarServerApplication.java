package com.simplecar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.simplecar.mapper")
public class SimpleCarServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleCarServerApplication.class, args);
    }
}
