package fr.gpmsi.pmsixml;

/**
 * Exception lancée lorsque la taille d'un champ n'est pas respectée 
 * (Le champ est trop petit et ne peut pas contenir la valeur).
 * @author hkaradimas
 *
 */
public class FieldSizeException
extends Exception 
{

	/**
	 * Pour la sérialisation éventuelle (pas utilisé dans ce projet cependant)
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur
	 */
	public FieldSizeException() {
	}

	/**
	 * Constructeur
	 * @param message _
	 */
	public FieldSizeException(String message) {
		super(message);
	}

	/**
	 * Constructeur
	 * @param cause _
	 */
	public FieldSizeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructeur
	 * @param message _
	 * @param cause _
	 */
	public FieldSizeException(String message, Throwable cause) {
		super(message, cause);
	}

	 /**
	  * Constructeur
	  * @param message Le message
	  * @param cause L'exception de départ
	  * @param enableSuppression Autoriser la suppression
	  * @param writableStackTrace Peut-on écrire dans la Stack Trace
	  */
	public FieldSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
