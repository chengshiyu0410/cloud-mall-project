package com.csy.cloud.order.service;

import org.springframework.stereotype.Component;

@Component
public class StdoutLogger extends com.p6spy.engine.spy.appender.StdoutLogger {
    public void logText(String text) {
        System.err.println(text);
    }
}