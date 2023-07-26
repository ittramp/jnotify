package org.throne.core.exception;

import org.throne.Jnotify;

/**
 * java执行系统函数的异常
 *
 * @author tramp
 * @date 2023/2/13 19:11
 */
public class JnotifyHandleInvokeErrorException extends RuntimeException {

    /**
     * @param functionName 方法名称
     * @param e            异常信息
     */
    public JnotifyHandleInvokeErrorException(String functionName, Throwable e) {
        super(functionName + " error,detail is :" + e.getMessage());
        Jnotify.getLogger().debug(" invoke function [" + functionName + "] error,detail is :" + e.getMessage());
    }
}
