package javgent.executor;

/**
 * Exception to stop the execution of something
 */
public class CancelExecutionException extends RuntimeException {
    public CancelExecutionException() {
    }

    public CancelExecutionException(String message) {
        super(message);
    }

    public CancelExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelExecutionException(Throwable cause) {
        super(cause);
    }

    public CancelExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
