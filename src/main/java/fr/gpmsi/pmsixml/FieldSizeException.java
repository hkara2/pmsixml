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

	public FieldSizeException() {
	}

	public FieldSizeException(String message) {
		super(message);
	}

	public FieldSizeException(Throwable cause) {
		super(cause);
	}

	public FieldSizeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
