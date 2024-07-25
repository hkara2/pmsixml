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
   * 
   */
  private static final long serialVersionUID = -8840601289040557999L;
  
  int errorCode = -1;
  
  int lineNr = -1; 
  
  public FieldParseException() {    
  }

  public FieldParseException(int errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public FieldParseException(String message) {
    super(message);    
  }

  public FieldParseException(Throwable cause) {
    super(cause);    
  }

  public FieldParseException(String message, Throwable cause) {
    super(message, cause);    
  }

  public FieldParseException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);    
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public String toString() {
    return super.toString()+(lineNr > 0 ? ", ligne " + lineNr : "")+", errorCode:"+errorCode;
  }

  public int getLineNr() {
    return lineNr;
  }

  public void setLineNr(int lineNr) {
    this.lineNr = lineNr;
  }
}
