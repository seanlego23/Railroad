package io.github.seanlego23.railroad.stations.selectionmethod;

public class InvalidSelectionException extends Exception {
	public InvalidSelectionException() {
		super();
	}

	public InvalidSelectionException(String message) {
		super(message);
	}

	public InvalidSelectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSelectionException(Throwable cause) {
		super(cause);
	}

	protected InvalidSelectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
