package com.csy.cloud.nacos.consumer.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Nacos 配置变更监听器
 * * 知识点串联：
 * 1. ApplicationRunner: Spring Boot 启动完成后执行的接口。我们需要在应用启动后，立刻向 Nacos 注册监听器。
 * 2. NacosConfigManager: Spring Cloud Alibaba 提供的核心管理器，通过它能获取到底层的 ConfigService。
 * 3. 观察者模式: 我们注册一个 Listener，当 Nacos 服务端配置发生变化时，会自动回调 receiveConfigInfo 方法。
 */
@Component
@Slf4j
public class ConfigChangeListener implements ApplicationRunner {

    @Autowired
    private NacosConfigManager nacosConfigManager;

    // 1. 注入 Nacos 核心配置类
    // 这个类会自动读取 bootstrap.yml 中 spring.cloud.nacos.config 下的所有属性
    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(">>> [自动监听] 开始扫描并注册所有配置文件的监听器...");

        // --- 1. 自动监听主配置 (私有配置) ---
        // 直接从 properties 里拿后缀和分组，比 @Value 更准
        String fileExtension = nacosConfigProperties.getFileExtension();
        String privateGroup = nacosConfigProperties.getGroup();
        // 兜底逻辑：如果没配 group，默认为 DEFAULT_GROUP
        if (!StringUtils.hasText(privateGroup)) {
            privateGroup = "DEFAULT_GROUP";
        }
        String privateDataId = applicationName + "." + fileExtension;

        registerListener(privateDataId, privateGroup);

        // --- 2. 自动监听共享配置 (Shared Configs) ---
        // 这里是关键！直接遍历 bootstrap.yml 里配置的 shared-configs 列表
        List<NacosConfigProperties.Config> sharedConfigs = nacosConfigProperties.getSharedConfigs();
        if (sharedConfigs != null) {
            for (NacosConfigProperties.Config config : sharedConfigs) {
                String group = StringUtils.hasText(config.getGroup()) ? config.getGroup() : "DEFAULT_GROUP";
                registerListener(config.getDataId(), group);
            }
        }

        // --- 3. 自动监听扩展配置 (Extension Configs) ---
        // Nacos 还支持 extension-configs，逻辑和 shared 一样，顺手也加上
        List<NacosConfigProperties.Config> extensionConfigs = nacosConfigProperties.getExtensionConfigs();
        if (extensionConfigs != null) {
            for (NacosConfigProperties.Config config : extensionConfigs) {
                String group = StringUtils.hasText(config.getGroup()) ? config.getGroup() : "DEFAULT_GROUP";
                registerListener(config.getDataId(), group);
            }
        }

        log.info(">>> [自动监听] 所有配置文件监听注册完毕！");
    }

    /**
     * 通用的注册监听方法
     */
    private void registerListener(String dataId, String group) {
        // 加个 try-catch 保证某一个文件失败不影响其他文件
        try {
            log.info("   -> 注册监听: [ {} / {} ]", group, dataId);
            nacosConfigManager.getConfigService().addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info(">>> [配置变更通知] 文件: [ {} / {} ] 已更新", group, dataId);
                    // 这里为了日志简洁，只打印前50个字符，或者你可以选择打印全量
                    log.info(">>> [更新摘要] {}", configInfo.length() > 100 ? configInfo.substring(0, 100) + "..." : configInfo);

                    // TODO: 写入审计日志表
                }
            });
        } catch (Exception e) {
            log.error("xxx 注册监听失败: [ {} / {} ]", group, dataId, e);
        }
    }
}