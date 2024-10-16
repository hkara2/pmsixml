package fr.gpmsi.pmsixml;

/**
 * Fabrique de stratégie de lecture des noeuds.
 * A partir du nom de noeud, donne l'objet stratégie à utiliser
 */
public class FszNodeReadStrategyFactory {
  
  /**
   * Méthode utilitaire utilisée pour construire un objet de stratégie de lecture
   * des fichiers à champs de longueur fixe, lorsqu'il y a un nombre de champs
   * variables, et des champs enfantsDefChamp.
   * On utilise un simple descripteur pour avoir l'objet approprié.
   * Voici la correspondance entre le descripteur et le champ ramené :
   * <ul>
   * <li>RHS, RHS1 : <code>FszNodeReadStrategyRHS1</code>
   * <li>RSA, RSA1 : <code>FszNodeReadStrategyRSA1</code>
   * <li>RSS, RSS1 : <code>FszNodeReadStrategyRSS1</code>
   * <li>VH, VH1 : <code>FszNodeReadStrategyVH1</code>
   * <li>MONO : <code>FszNodeReadStrategyMONO</code>
   * </ul>
   * @param d
   * @return
   */
  @SuppressWarnings("deprecation")
static FszNodeReadStrategy makeFszNodeReadStrategy(String d) {
    if (d == null) return null;
    d = d.toUpperCase();
    if (d.equals("RHS") || d.equals("RHS1")) return new FszNodeReadStrategyRHS1();
    if (d.equals("RSA") || d.equals("RSA1")) return new FszNodeReadStrategyRSA1();
    if (d.equals("RSS") || d.equals("RSS1")) return new FszNodeReadStrategyRSS1();
    if (d.equals("VH") || d.equals("VH1") || d.equals("VIDHOSP")) return new FszNodeReadStrategyVH1();
    if (d.equals("MONO")) return new FszNodeReadStrategyMONO();
    if (d.equals("NX")) return new FszNodeReadStrategyNX1();
    //pas de correspondance, renvoyer null
    return null;
  }

}
