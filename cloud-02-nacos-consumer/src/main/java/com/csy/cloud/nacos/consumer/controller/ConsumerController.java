package com.csy.cloud.nacos.consumer.controller;

import com.csy.cloud.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_PROVIDER = "http://nacos-provider";

    @GetMapping("/echo")
    public Result<String> echo () {
        return restTemplate.getForObject(SERVICE_PROVIDER + "/nacos-provider/echo", Result.class);
    }

    @GetMapping("/config/info")
    public Result<String> info () {
        return restTemplate.getForObject(SERVICE_PROVIDER + "/nacos-provider/config/info", Result.class);
    }
}
