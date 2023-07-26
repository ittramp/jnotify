package com.throne.jnotifyspringbootdemo.demo;

import com.throne.jnotifyspringbootdemo.demo.common.CustomLogger;
import com.throne.jnotifyspringbootdemo.demo.common.JnotifyListenerSample;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.throne.Jnotify;
import org.throne.core.exception.JnotifyException;
import org.throne.core.log.LoggerLevel;

/**
 * @author tramp
 * @date 2023/2/20 16:40
 */
@Slf4j
//@Component
public class CustomLoggerDemo {
    private int watchId;

    @PostConstruct
    public void init() {
        try {
            System.setProperty("jnotify.log.impl.override", CustomLogger.class.getName());
            Jnotify.setLoggerLevel(LoggerLevel.DEBUG);
            watchId = Jnotify.addWatch("/home/tramp/inotify", new JnotifyListenerSample());

        } catch (JnotifyException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() throws JnotifyException {
        Jnotify.removeWatch(watchId);
    }
}
