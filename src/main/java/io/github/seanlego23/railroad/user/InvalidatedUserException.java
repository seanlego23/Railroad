package io.github.seanlego23.railroad.user;

public class InvalidatedUserException extends RuntimeException {
	public InvalidatedUserException() {
		super();
	}

	public InvalidatedUserException(String message) {
		super(message);
	}

	public InvalidatedUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidatedUserException(Throwable cause) {
		super(cause);
	}

	protected InvalidatedUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
