package fr.gpmsi.pmsixml;

/**
 * Emettre un RHS sous forme textuelle.
 * L'utilisation des métadonnées permet aussi en plus de faciliter la lecture, de faire aussi plus 
 * facilement l'écriture des fichiers PMSI.
 * Ici depuis un FszGroup qui représente des données de RHS le RhsWriter va permettre de faire l'écriture
 * d'une ligne de RHS au bon format.
 * @author hkaradimas
 *
 */
public class RhsWriter {

  /**
   * Constructeur simple
   */
  public RhsWriter() {
  }

  /**
   * Ecrit un RHS à partir du groupe.
   * @param rhs Le {@link FszGroup} qui contient le RHS
   * @param sb Le {@link StringBuffer} dans lequel écrire
   * @throws FieldParseException Si erreur d'analyse du groupe
   * @throws FieldSizeException Si erreur de taille d'un des champs
   */
  public void writeRhs(FszGroup rhs, StringBuffer sb) 
  throws FieldParseException, FieldSizeException
  {
    //RHS
    //  DA   NDA
    //  ACS  NCSA
    //  ACC  NCCA
    //Commencer par mettre a jour les champs de compteur
    //DA (diagnostics associés)
    FszNode das = rhs.getChild("DA");
    int dasCount = das.getChildCount();
    FszField ndaNd = (FszField) rhs.getChild("NDA");
    ndaNd.setValue(Integer.toString(dasCount));
    //ACS (actes CSARR)
    FszNode acss = rhs.getChild("ACS");
    int acsCount = acss.getChildCount();
    FszField ncsaNd = (FszField) rhs.getChild("NCSA");
    ncsaNd.setValue(Integer.toString(acsCount));
    //ACC (actes CCAM)
    FszNode accs = rhs.getChild("ACC");
    int accCount = accs.getChildCount();
    FszField nccaNd = (FszField) rhs.getChild("NCCA");
    nccaNd.setValue(Integer.toString(accCount));
    //emettre maintenant les textes dans l'ordre
    rhs.toText(sb);
    for (FszNode child : das.getChildren()) child.toText(sb);
    for (FszNode child : acss.getChildren()) child.toText(sb);    
    for (FszNode child : accs.getChildren()) child.toText(sb);
  }
  
}
