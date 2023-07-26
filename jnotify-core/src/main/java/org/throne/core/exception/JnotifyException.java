package org.throne.core.exception;

import java.io.Serializable;

/**
 * 文件监听异常基础类
 *
 * @author tramp
 */
public abstract class JnotifyException extends Exception implements Serializable {

    protected final int systemErrorCode;

    public JnotifyException(String s, int errorCode) {
        super(s);
        systemErrorCode = errorCode;
    }

    public int getSystemError() {
        return systemErrorCode;
    }

//    public abstract int getErrorCode();
}
