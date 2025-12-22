package com.csy.cloud.openfeign.Filter;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

//需要在所有远程调用中自动携带 Token、TraceID 或特定 Header。
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer 123456");
        requestTemplate.header("TraceID","csy");


    }
}
