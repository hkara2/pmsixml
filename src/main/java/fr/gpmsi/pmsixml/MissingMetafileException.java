package fr.gpmsi.pmsixml;

/** Exception si fichier de métadonnée absent */
public class MissingMetafileException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/** constructeur par défaut */
	public MissingMetafileException() {
	}

	/** 
	 * Constructeur avec message
	 * @param message Le message
	 */
	public MissingMetafileException(String message) {
		super(message);
	}

	/**
	 * Constructeur avec cause
	 * @param cause La cause
	 */
	public MissingMetafileException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constucteur avec message et cause
	 * @param message Le message
	 * @param cause La cause
	 */
	public MissingMetafileException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructeur complet
	 * @param message Le message
	 * @param cause La cause
	 * @param enableSuppression Autoriser la suppression
	 * @param writableStackTrace StrackTrace en écriture 
	 */
	public MissingMetafileException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
