package org.joy.core.exception;

import java.io.Serializable;

/**
 * @author tramp
 * @date 2023/2/17 18:09
 */
public class JnotifyRuntimeException extends RuntimeException implements Serializable {
    public JnotifyRuntimeException(String message) {
        super(message);
    }

    public JnotifyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JnotifyRuntimeException(Throwable cause) {
        super(cause);
    }
}
