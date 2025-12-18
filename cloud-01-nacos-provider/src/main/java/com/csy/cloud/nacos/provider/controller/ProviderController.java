package com.csy.cloud.nacos.provider.controller;


import com.csy.cloud.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/echo")
    public Result<String> echo (){
        return Result.success("Hello Nacos Discovery, I am from port: " + serverPort + ", received: 你好");
    }
}
