package io.github.seanlego23.railroad.stations.selectionmethod;

public class InvalidDataTypeException extends Exception {

	public InvalidDataTypeException() {
		super();
	}

	public InvalidDataTypeException(String message) {
		super(message);
	}

	public InvalidDataTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDataTypeException(Throwable cause) {
		super(cause);
	}

	protected InvalidDataTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
