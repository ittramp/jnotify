package org.throne.adapter.linux.exception;

import org.throne.core.exception.JnotifyRuntimeException;

/**
 * @author tramp
 * @date 2023/2/17 18:11
 */
public class JnotifyLinuxRuntimeException extends JnotifyRuntimeException {

    public JnotifyLinuxRuntimeException(String message) {
        super(message);
    }
}
