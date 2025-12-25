package com.csy.cloud.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.csy.cloud.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class FlowLimitController {



    // ==================== 1. 流控测试 (QPS) ====================

    /**
     * 测试A：普通接口，用于测试 QPS 限流
     * 场景：设置 QPS 单机阈值为 1，快速刷新浏览器即可触发 BlockException
     */
    @GetMapping("/testA")
    public Result<String> testA() {
        return Result.success("testA ----- (正常接口)");
    }

    /**
     * 测试B：关联流控测试
     * 场景：当 /testB 的访问量太大时，限制 /testA (或者反过来，视配置而定)
     */
    @GetMapping("/testB")
    public Result<String> testB() {
        return Result.success("testB ----- (关联接口)");
    }

    // ==================== 2. 熔断降级测试 ====================

    /**
     * 测试D：慢调用熔断
     * 场景：Sentinel 控制台设置 [慢调用比例]、最大RT=200ms、比例阈值=0.5、最小请求数=5
     * 效果：用 JMeter 压测该接口，前几次请求会很慢（因为睡了1秒），触发熔断后，后续请求会被 Sentinel 直接拦截（快速失败），不再等待1秒。
     */
    @GetMapping("/testD")
    public Result<String> testD() {
        try {
            // 模拟业务耗时 1 秒
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("testD 测试慢调用...");
        return Result.success("testD ----- (慢调用)");
    }

    /**
     * 测试E：异常比例/异常数熔断
     * 场景：Sentinel 控制台设置 [异常比例]、比例阈值=0.2、最小请求数=5
     * 效果：疯狂请求该接口，当异常达到比例后，服务熔断，后续请求不再抛出 RuntimeException，而是被 Sentinel 拦截。
     */
    @GetMapping("/testE")
    public Result<String> testE() {
        log.info("testE 测试异常...");
        // 模拟 100% 异常
        int age = 10 / 0;
        return Result.success("testE ----- (异常测试)");
    }

    // ==================== 3. 热点参数限流测试 ====================

    /**
     * 测试HotKey：热点参数限流
     * @SentinelResource: 定义资源名，blockHandler 指定兜底方法
     * 场景：设置热点规则，资源名 "testHotKey"，参数索引 0 (即 p1)，阈值 1。
     * 效果：
     * 请求 /testHotKey?p1=a&p2=b (QPS > 1 时触发限流，走 deal_testHotKey)
     * 请求 /testHotKey?p2=b      (不带 p1，不限流)
     */
    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey", blockHandler = "deal_testHotKey")
    public Result<String> testHotKey(@RequestParam(value = "p1", required = false) String p1,
                                     @RequestParam(value = "p2", required = false) String p2) {
        return Result.success("testHotKey ----- (热点参数测试)");
    }

    /**
     * 兜底方法 (BlockHandler)
     * 要求：
     * 1. 方法返回值类型必须与原方法一致
     * 2. 参数列表必须与原方法一致，最后多一个 BlockException 参数
     */
    public Result<String> deal_testHotKey(String p1, String p2, BlockException exception) {
        return Result.fail("o(╥﹏╥)o testHotKey 被限流了，这是兜底返回");
    }
}
