package com.csy.cloud.openfeign.config;


import feign.Logger;
import org.springframework.context.annotation.Bean;

// 注意：不加 @Configuration，这样通过 @FeignClient(configuration=...) 局部引用
public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        // NONE, BASIC, HEADERS, FULL
        return Logger.Level.FULL;
    }
}
