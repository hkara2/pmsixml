package fr.gpmsi.pmsixml.nx;

public class NxParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NxParseException() {
		super();
	}

	public NxParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NxParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public NxParseException(String message) {
		super(message);
	}

	public NxParseException(Throwable cause) {
		super(cause);
	}

}
