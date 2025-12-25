package com.csy.cloud.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简易版 Java 压测工具 (Low-code JMeter)
 * * 功能：
 * 1. 模拟多线程并发请求 (并发数可配)
 * 2. 模拟持续压测 (循环次数或时间可配)
 * 3. 统计 QPS 和 响应时间
 */
public class JMeterTool {

    // ================= 配置区域 =================
    // 目标接口 (修改这里)
//     private static final String TARGET_URL = "http://100.89.225.23:9020/testA"; // 普通接口
    private static final String TARGET_URL = "http://100.89.225.23:9020/testD"; // 慢调用接口
    // private static final String TARGET_URL = "http://localhost:8401/testE"; // 异常接口


    private static final int CONCURRENT_THREADS = 10; // 并发线程数 (模拟多少用户)
    private static final int TOTAL_REQUESTS_PER_THREAD = 1000; // 每个线程最大请求次数 (防死循环兜底)
    private static final int DURATION_SECONDS = 10;   // 持续压测时间 (秒) - 设为 0 则只看请求次数
    private static final int INTERVAL_MS = 100;        // 每次请求间隔 (毫秒)
    // ===========================================

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failCount = new AtomicInteger(0);
    private static final AtomicBoolean isRunning = new AtomicBoolean(true); // 全局运行标志

    public static void main(String[] args) throws InterruptedException {
        System.out.println(">>> 开始压测: " + TARGET_URL);
        System.out.println(">>> 线程数: " + CONCURRENT_THREADS);
        if (DURATION_SECONDS > 0) {
            System.out.println(">>> 模式: 持续压测 " + DURATION_SECONDS + " 秒");
        } else {
            System.out.println(">>> 模式: 固定次数 " + (CONCURRENT_THREADS * TOTAL_REQUESTS_PER_THREAD) + " 次");
        }

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        long startTime = System.currentTimeMillis();

        // 如果设置了持续时间，开启一个守护线程来控制停止
        if (DURATION_SECONDS > 0) {
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(DURATION_SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isRunning.set(false);
                System.out.println("\n>>> [时间到] 停止压测...");
                executor.shutdownNow(); // 强行尝试停止所有任务
            }).start();
        }

        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < TOTAL_REQUESTS_PER_THREAD; j++) {
                    // 双重检查：如果时间到了，或者手动停止了，就退出循环
                    if (!isRunning.get()) {
                        break;
                    }
                    sendRequest();
                    try {
                        if (INTERVAL_MS > 0) {
                            Thread.sleep(INTERVAL_MS);
                        }
                    } catch (InterruptedException e) {
                        // 线程被中断（比如 shutdownNow），直接退出
                        break;
                    }
                }
            });
        }

        executor.shutdown();

        // 主线程循环打印统计信息，直到任务结束
        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            printStats(startTime);
            // 如果没设置时间限制，但所有请求跑完了，也会自然退出
            if (executor.isTerminated()) {
                break;
            }
        }

        System.out.println("\n>>> 压测结束！");
        printStats(startTime);
    }

    private static void sendRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TARGET_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 200) {
                successCount.incrementAndGet();
                // 成功时可以偶尔打印一下，避免刷屏太快
                // System.out.println("[SUCCESS] " + response.body());
            } else {
                failCount.incrementAndGet();
                // 失败时打印详细信息，方便排查是 429 限流还是 500 报错
                System.err.println("[FAIL] Status: " + status + " | Body: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            failCount.incrementAndGet();
            System.err.println("[ERROR] Request Error: " + e.getMessage());
        }
    }

    private static void printStats(long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        int s = successCount.get();
        int f = failCount.get();
        int total = s + f;

        if (duration > 0) {
            double qps = (double) total / duration * 1000;
            System.out.printf("\r[压测中] 耗时: %ds | 成功: %d | 失败: %d | QPS: %.2f", (duration / 1000), s, f, qps);
        }
    }
}