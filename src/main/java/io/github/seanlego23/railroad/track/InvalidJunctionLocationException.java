package io.github.seanlego23.railroad.track;

public class InvalidJunctionLocationException extends Exception {
	public InvalidJunctionLocationException() {
		super();
	}

	public InvalidJunctionLocationException(String message) {
		super(message);
	}

	public InvalidJunctionLocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidJunctionLocationException(Throwable cause) {
		super(cause);
	}

	protected InvalidJunctionLocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
