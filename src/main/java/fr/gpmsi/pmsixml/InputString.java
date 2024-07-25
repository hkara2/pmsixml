package fr.gpmsi.pmsixml;

/**
 * Un objet pour avoir la ligne d'entrée, avec les informations qui lui correspondent,
 * notamment le numéro de ligne, et la position.
 * Cela permet de donner des messages d'erreur parlants.
 * @author hkaradimas
 *
 */
public class InputString
{
  public String line; //ligne en cours d'analyse
  public int lineNumber; //numero de la ligne, pour les messages d'erreur
  public int pos; //position dans la ligne, commence a 0
  public boolean acceptTruncated = true; //est-ce qu'on accepte les lignes tronquees, defaut : vrai
  
  public InputString() {}
  
  public InputString(String line) {
    this.line = line;
  }
  
  /**
   * Si on réutilise l'objet "InputString", appeler cette méthode pour passer à la ligne suivante.
   * Cette méthode incrémente la ligne et remet pos à 0.
   * @param line la ligne suivante, que InputString va stocker dans this.line
   */
  public void nextLine(String line) {
    this.line = line;
    lineNumber++;
    pos = 0;
  }

  public boolean isTruncatedInputAccepted() {
    return acceptTruncated;
  }

  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.acceptTruncated = truncatedInputAccepted;
  }
}
