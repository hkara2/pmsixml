package fr.gpmsi.pmsixml.nx;

/**
 * Exception envoyée lorsqu'il y a une erreur lors de l'analyse d'un fichier de métadonnées.
 */
public class NxMetaParseException
extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur simple
	 */
	public NxMetaParseException() {
	}

	/**
	 * Constructeur avec message
	 * @param message Le message
	 */
	public NxMetaParseException(String message) {
		super(message);
	}

	/**
	 * Constructeur à partir d'une exception
	 * @param cause L'exception qui cause cette exception
	 */
	public NxMetaParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructeur avec message et exception de départ
	 * @param message Le message
	 * @param cause La cause
	 */
	public NxMetaParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * constructeur complet
	 * @param message Le message
	 * @param cause La cause
	 * @param enableSuppression Autoriser la suppression si true
	 * @param writableStackTrace On peut écrire dans la stack trace si true
	 */
	public NxMetaParseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
