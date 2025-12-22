package com.csy.cloud.openfeign.serivce;

import com.csy.cloud.openfeign.config.FeignConfig;
import com.csy.cloud.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * OpenFeign 接口定义
 * * 知识点：
 * 1. value: 指定 Nacos 中的服务名。
 * 2. path: 【关键】指定统一的上下文路径前缀。
 * 如果 Provider 配置了 server.servlet.context-path=/nacos-provider
 * 或者 Controller 类上有 @RequestMapping("/nacos-provider")
 * 这里配置 path 后，下面所有方法的 URL 都会自动拼接上这个前缀。
 */
@FeignClient(value = "nacos-provider", configuration = FeignConfig.class, path = "/nacos-provider")
public interface RemoteProviderService {

    // 实际请求 URL: http://nacos-provider/nacos-provider/echo
    // OpenFeign 会自动拼接: path + GetMapping = /nacos-provider + /echo
    @GetMapping("/echo")
    Result<String> echo();

    // 实际请求 URL: http://nacos-provider/nacos-provider/config/info
    @GetMapping("/config/info")
    Result<String> getConfigInfo();
}