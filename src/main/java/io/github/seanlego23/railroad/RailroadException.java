package io.github.seanlego23.railroad;

/**
 * Represents some type of internal Railroad Error.
 */
public class RailroadException extends RuntimeException {
    public RailroadException() {
        super();
    }

    public RailroadException(String message) {
        super(message);
    }

    public RailroadException(String message, Throwable cause) {
        super(message, cause);
    }

    public RailroadException(Throwable cause) {
        super(cause);
    }

    protected RailroadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
