package com.throne.jnotifyspringbootdemo.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.throne.core.log.ILogger;

/**
 * @author tramp
 * @date 2023/2/20 16:37
 */
@Slf4j
public class CustomLogger implements ILogger {

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }
}
