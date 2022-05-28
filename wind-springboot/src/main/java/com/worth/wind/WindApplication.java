package com.worth.wind;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 启动类
 *
 * @author yezhiqiu
 * @date 2021/08/14
 */
@MapperScan({"com.worth.wind.**.dao"})
@SpringBootApplication
@EnableScheduling
public class WindApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
