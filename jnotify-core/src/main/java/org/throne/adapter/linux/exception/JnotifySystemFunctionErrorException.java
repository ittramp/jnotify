package org.throne.adapter.linux.exception;

import org.throne.core.exception.JnotifyException;

/**
 * 调用系统函数后出现的问题，
 * 通过获取系统errorno以及对应的系统错误信息说明来抛出异常
 *
 * @author tramp
 * @date 2023/2/15 18:12
 */
public class JnotifySystemFunctionErrorException extends JnotifyException {
    public JnotifySystemFunctionErrorException(String errorMessage, int errorNumber) {
        super(errorMessage, errorNumber);
    }
}
