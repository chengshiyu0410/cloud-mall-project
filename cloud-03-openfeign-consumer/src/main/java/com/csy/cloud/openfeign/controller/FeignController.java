package com.csy.cloud.openfeign.controller;

import com.csy.cloud.openfeign.serivce.RemoteProviderService;
import com.csy.cloud.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    private static final Logger log = LoggerFactory.getLogger(FeignController.class);
    @Autowired
    private RemoteProviderService remoteProviderService;


    @GetMapping("/feign/echo")
    public Result<String> echo(){
        log.info(">>> 正在使用OpenFegin 发起远程调用。。。。。");
        return remoteProviderService.echo();
    }
}
