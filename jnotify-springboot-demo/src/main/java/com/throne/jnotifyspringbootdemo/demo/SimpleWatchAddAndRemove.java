package com.throne.jnotifyspringbootdemo.demo;

import com.throne.jnotifyspringbootdemo.demo.common.JnotifyListenerSample;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.throne.Jnotify;
import org.throne.core.exception.JnotifyException;

/**
 * @author tramp
 * @date 2023/2/19 11:21
 */
//@Component
@Slf4j
public class SimpleWatchAddAndRemove {

    private int watchId = 0;

    @PostConstruct
    public void init() {
        try {
            int watchId = Jnotify.addWatch("/home/tramp/inotify", new JnotifyListenerSample());
        } catch (JnotifyException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            Jnotify.removeWatch(watchId);
        } catch (JnotifyException e) {
            throw new RuntimeException(e);
        }
    }
}
