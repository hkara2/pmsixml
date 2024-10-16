package fr.gpmsi.pmsixml;

/**
 * Un objet pour avoir la ligne d'entrée, avec les informations qui lui correspondent,
 * notamment le numéro de ligne, et la position.
 * Cela permet de donner des messages d'erreur parlants.
 * Utilisé par les objets du package, peu utile en dehors du package.
 * @author hkaradimas
 *
 */
public class InputString
{
  /** ligne en cours d'analyse */
  public String line; 
  /** numero de la ligne, pour les messages d'erreur */
  public int lineNumber;
  /** position dans la ligne, commence a 0 */
  public int pos; 
  /** est-ce qu'on accepte les lignes tronquees, defaut : vrai */
  public boolean acceptTruncated = true; 
  
  /**
   * Constructeur simple
   */
  public InputString() {}
  
  /**
   * Constructeur avec une ligne
   * @param line La ligne
   */
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

  /**
   * Est-ce que les entrées tronquées sont acceptées
   * @return true si elles le sont
   */
  public boolean isTruncatedInputAccepted() {
    return acceptTruncated;
  }

  /**
   * Est-ce que les entrées tronquées sont acceptées
   * @param truncatedInputAccepted true si elles le sont
   */
  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.acceptTruncated = truncatedInputAccepted;
  }
}
