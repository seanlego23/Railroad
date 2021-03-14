package io.github.seanlego23.railroad.connection;

public class IllegalConnectionException extends Exception {
    public IllegalConnectionException() {
        super();
    }

    public IllegalConnectionException(String message) {
        super(message);
    }

    public IllegalConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConnectionException(Throwable cause) {
        super(cause);
    }

    protected IllegalConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
