package com.joydada;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.joydada.mapper") //设置mapper接口的扫描包
@SpringBootApplication
public class MinijwxtApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinijwxtApplication.class, args);
    }

}
