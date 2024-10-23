package fr.gpmsi.pmsixml;

import java.math.BigDecimal;

/**
 * Quelques utilitaires pour les nombres.
 * @author hkaradimas
 *
 */
public class NumUtils {
  
  /**
   * Nettoyage d'une chaîne qui représente un nombre.
   * Suppression des espaces, et des zéros de début.
   * @param n la chaîne qui représente normalement un nombre
   * @return la chaîne nettoyée
   */
  public static String cleanup(String n) {
    if (n == null) return n;
    n = n.trim();
    int ix = 0;
    while (ix < n.length() && n.charAt(ix) == '0') ix++;
    n = n.substring(ix, n.length());
    return n;
  }

  /**
   * Lire une chaîne de caractères en tant que nombre en la nettoyant puis en la lisant
   * en tant que BigDecimal. Cela permet de lire des nombres très grands sans problèmes.
   * @param n le nombre en chaine de caractères
   * @return le BigDecimal équivalent
   */
  public static BigDecimal parse(String n) {
    n = cleanup(n);
    if (n == null) return null;
    if (n.length() == 0) return null;
    return new BigDecimal(n);
  }
  
  /**
   * N'y a-t-il que des zéros dans cette chaine de caractères
   * @param str la chaine de caractères à utiliser
   * @return true si la chaîne n'est composée que de zéros
   */
  static boolean onlyZeroes(String str) {
    for (int i = 0; i < str.length(); i++) if (str.charAt(i) != '0') return false;
    return true;
  }
  
  /** Constructeur privé car cette classe n'a que des méthodes statiques */
  private NumUtils() {}
}
