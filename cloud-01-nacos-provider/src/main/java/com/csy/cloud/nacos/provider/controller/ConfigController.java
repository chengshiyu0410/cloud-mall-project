package com.csy.cloud.nacos.provider.controller;


import com.csy.cloud.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope // 核心注解！只有加了这个，Nacos配置更新时，这个类的变量才会自动刷新
public class ConfigController {

    // 我们将在 Nacos 控制台配置这个值
    @Value("${config.info:这是本地默认值}")
    private String configInfo;

    @GetMapping("/config/info")
    public Result<String> getConfigInfo() {
        return Result.success(configInfo);
    }
}