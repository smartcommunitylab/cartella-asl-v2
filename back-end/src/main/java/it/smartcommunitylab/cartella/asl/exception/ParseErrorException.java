package it.smartcommunitylab.cartella.asl.exception;

public class ParseErrorException extends Exception {

	public ParseErrorException() {
		super();
	}

	public ParseErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParseErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseErrorException(String message) {
		super(message);
	}

	public ParseErrorException(Throwable cause) {
		super(cause);
	}

}