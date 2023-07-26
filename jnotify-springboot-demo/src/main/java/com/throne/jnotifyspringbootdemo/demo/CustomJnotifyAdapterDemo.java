package com.throne.jnotifyspringbootdemo.demo;

import com.throne.jnotifyspringbootdemo.demo.common.CustomJnotifyAdapterLinux;
import com.throne.jnotifyspringbootdemo.demo.common.JnotifyListenerSample;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.throne.Jnotify;
import org.throne.core.exception.JnotifyException;

/**
 * @author tramp
 * @date 2023/2/20 17:17
 */
@Slf4j
//@Component
public class CustomJnotifyAdapterDemo {
    private int watchId;

    @PostConstruct
    public void init() {
        try {
            System.setProperty("jnotify.impl.override", CustomJnotifyAdapterLinux.class.getName());
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
