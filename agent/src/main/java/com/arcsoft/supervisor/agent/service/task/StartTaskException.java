package com.arcsoft.supervisor.agent.service.task;

/**
 * Exception throw when failed to start task.
 *
 * @author zw.
 */
public class StartTaskException extends Exception {

    public StartTaskException(Throwable cause) {
        super(cause);
    }

    public StartTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartTaskException(String message) {
        super(message);
    }

    public StartTaskException() {
        super();
    }
}
