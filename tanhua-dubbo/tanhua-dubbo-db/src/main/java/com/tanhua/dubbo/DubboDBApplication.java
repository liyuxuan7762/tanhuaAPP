package com.tanhua.dubbo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tanhua.dubbo.mappers")
public class DubboDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboDBApplication.class,args);
    }
}
