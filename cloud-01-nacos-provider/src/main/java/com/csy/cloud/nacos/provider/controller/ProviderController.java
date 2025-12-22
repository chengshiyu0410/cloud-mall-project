package com.csy.cloud.nacos.provider.controller;


import com.csy.cloud.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;

@RestController
@Slf4j
public class ProviderController {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/echo")
    public Result<String> echo(HttpServletRequest request) {
        // --- 验证逻辑开始 ---
        String token = request.getHeader("Authorization");
        String traceId = request.getHeader("TraceID");

        log.info("================== Header 验证 ==================");
        log.info("端口: {}", serverPort);
        log.info("Authorization: {}", token);
        log.info("TraceID: {}", traceId);
        log.info("================================================");
        // --- 验证逻辑结束 ---


        return Result.success("Hello Nacos, I am from port: " + serverPort +
                ", 收到 TraceID: " + traceId); // 把 TraceID 返回回去，在浏览器也能看到
    }
}
