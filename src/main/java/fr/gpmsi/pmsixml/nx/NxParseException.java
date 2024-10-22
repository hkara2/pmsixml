package fr.gpmsi.pmsixml.nx;

/**
 * Exception lancée lorsqu'il y a une erreur d'analyse du format NX.
 */
public class NxParseException extends Exception {

	/**
	 * pour la sérialisation
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructeur simple
	 */
	public NxParseException() {
		super();
	}

	/**
	 * Constructeur parent
	 * @param message _
	 * @param cause _
	 * @param enableSuppression _
	 * @param writableStackTrace _
	 */
	public NxParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Constructeur parent
	 * @param message _
	 * @param cause _
	 */
	public NxParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructeur parent
	 * @param message _
	 */
	public NxParseException(String message) {
		super(message);
	}

	/**
	 * Constructeur parent
	 * @param cause _
	 */
	public NxParseException(Throwable cause) {
		super(cause);
	}

}
