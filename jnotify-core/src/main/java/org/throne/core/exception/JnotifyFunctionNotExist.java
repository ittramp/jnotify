package org.throne.core.exception;

/**
 * @author tramp
 * @date 2023/2/17 10:01
 */
public class JnotifyFunctionNotExist extends RuntimeException {

    public JnotifyFunctionNotExist(String functionName) {
        super("function name not found in system ,function is " + functionName);
    }
}
