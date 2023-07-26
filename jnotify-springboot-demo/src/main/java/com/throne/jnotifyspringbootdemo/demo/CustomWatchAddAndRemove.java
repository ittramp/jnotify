package com.throne.jnotifyspringbootdemo.demo;

import com.throne.jnotifyspringbootdemo.demo.common.JnotifyListenerSample;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.throne.Jnotify;
import org.throne.core.JnotifyEventTypeEnum;
import org.throne.core.exception.JnotifyException;
import org.throne.core.log.LoggerLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tramp
 * @date 2023/2/20 17:49
 */
@Component
@Slf4j
public class CustomWatchAddAndRemove {
    private int watchId = 0;

    @PostConstruct
    public void init() {
        try {

            // ===============更多的拓展，可以自定义具体监听类型，可以定义是否需要递归监视===============
            Jnotify.setLoggerLevel(LoggerLevel.DEBUG);
            List<JnotifyEventTypeEnum> eventTypes = new ArrayList<>() {{
                add(JnotifyEventTypeEnum.FILE_CREATED);
                add(JnotifyEventTypeEnum.FILE_DELETED);
                add(JnotifyEventTypeEnum.FILE_MODIFIED);
                add(JnotifyEventTypeEnum.FILE_RENAMED);
            }};
            //不进行递归监视
            boolean watchSubtree = false;
            watchId = Jnotify.addWatch("/home/tramp/inotify", eventTypes, watchSubtree, new JnotifyListenerSample());

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
