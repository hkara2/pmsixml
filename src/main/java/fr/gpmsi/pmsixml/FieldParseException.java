package fr.gpmsi.pmsixml;

/**
 * Exception lors de l'analyse d'un champ de fichier à positions fixes.
 * L'information de ligne peut être rajoutée pour donner de meilleurs
 * messages d'erreur.
 * @author hkaradimas
 *
 */
public class FieldParseException
extends Exception
{
  /**
   * Pour sérialisation
   */
  private static final long serialVersionUID = -8840601289040557999L;
  
  /**
   * Code erreur
   */
  int errorCode = -1;
  
  /**
   * Numéro de ligne
   */
  int lineNr = -1; 
  
  /**
   * Constructeur simple
   */
  public FieldParseException() {    
  }

  /**
   * Constructeur avec code d'erreur et message
   * @param errorCode Code d'erreur
   * @param message Message
   */
  public FieldParseException(int errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Constructeur avec juste le message
   * @param message Le message
   */
  public FieldParseException(String message) {
    super(message);    
  }

  /**
   * Constructeur avec juste la cause de cette exception
   * @param cause La cause
   */
  public FieldParseException(Throwable cause) {
    super(cause);    
  }

  /**
   * Constructeur avec message et cause
   * @param message Le message
   * @param cause La cause
   */
  public FieldParseException(String message, Throwable cause) {
    super(message, cause);    
  }

  /**
   * Constructeur complet 
   * @see Exception
   * @param message _
   * @param cause _
   * @param enableSuppression Suppression autorisée ou non (cf. javadoc pour Exception)
   * @param writableStackTrace Rendre la stack trace modifiable (cf. javadoc pour Exception)
   */
  public FieldParseException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);    
  }

  /**
   * Code erreur
   * @return Le code erreur
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * Définir le code erreur
   * @param errorCode Le code erreur
   */
  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public String toString() {
    return super.toString()+(lineNr > 0 ? ", ligne " + lineNr : "")+", errorCode:"+errorCode;
  }

  /**
   * Retourner le numéro de ligne
   * @return Le numéro de ligne
   */
  public int getLineNr() {
    return lineNr;
  }

  /**
   * Définir le numéro de ligne
   * @param lineNr Le numéro de ligne
   */
  public void setLineNr(int lineNr) {
    this.lineNr = lineNr;
  }
}
