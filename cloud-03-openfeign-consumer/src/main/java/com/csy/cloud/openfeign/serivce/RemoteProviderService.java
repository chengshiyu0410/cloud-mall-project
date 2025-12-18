package com.csy.cloud.openfeign.serivce;

import com.csy.cloud.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 知识点解析：
 * 1. @FeignClient: 声明这是一个 Feign 客户端。
 * 2. value = "nacos-provider": 指定要去调用的微服务名称 (Nacos 里注册的那个名字)。
 * 3. 接口定义: 必须和 Provider 的 Controller 方法签名保持一致 (URL, 参数, 返回值)。
 */
@FeignClient(value = "nacos-provider")
public interface RemoteProviderService {

    // 这里直接复制 ProviderController 里的方法签名即可
    // 注意：@GetMapping 里的路径是 Provider 的全路径
    @GetMapping("/nacos-provider/echo")
    Result<String> echo();

    // 假设 Provider 以后有了 getById
    // @GetMapping("/order/{id}")
    // Result<Order> getOrderById(@PathVariable("id") Long id);
}
