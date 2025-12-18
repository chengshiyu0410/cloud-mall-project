package com.csy.cloud.nacos.consumer.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * 知识点解析：
     * @LoadBalanced 注解赋予了 RestTemplate "智慧"。
     * 1. 拦截请求：它会拦截 HTTP 请求。
     * 2. 解析服务名：将 URL 中的 "nacos-provider" 解析出来。
     * 3. 查找列表：去 Nacos 获取 "nacos-provider" 对应的所有 IP 列表 (例如 192.168.1.5:9001, 192.168.1.6:9001)。
     * 4. 负载均衡：根据算法（默认轮询）选择一个 IP，替换原来的 URL。
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
