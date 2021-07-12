package com.bookportal.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

@SpringBootApplication
public class AppMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppMediaApplication.class, args);
    }

}
