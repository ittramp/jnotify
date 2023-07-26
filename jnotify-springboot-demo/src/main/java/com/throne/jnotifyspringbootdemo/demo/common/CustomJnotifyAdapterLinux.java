package com.throne.jnotifyspringbootdemo.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.throne.adapter.linux.JnotifyAdapterLinux;
import org.throne.adapter.linux.exception.JnotifySystemFunctionErrorException;

/**
 * @author tramp
 * @date 2023/2/20 17:12
 */
@Slf4j
public class CustomJnotifyAdapterLinux extends JnotifyAdapterLinux {
    public CustomJnotifyAdapterLinux() {
        super();
    }

    @Override
    protected void notifyChangeEvent(String name, Integer watchDescriptor, Integer mask, Integer cookie) throws JnotifySystemFunctionErrorException {
        log.error("监听到文件变化事件，事件信息为：name： 【{}】，watchDescriptor： 【{}】，mask：【{}】，cookie：【{}】，"
                , name, watchDescriptor, mask, cookie);
    }
}
