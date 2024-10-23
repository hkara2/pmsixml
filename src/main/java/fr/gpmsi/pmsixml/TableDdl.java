package fr.gpmsi.pmsixml;

import java.util.Iterator;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Produire un script de définition de données (DDL) pour du SQL à partir d'un FszGroup.
 * Génère des types de données qui correspondent aux besoins du PMSI.
 * Correspondance des types produits :
 * <dl>
 * <dt>N
 * <dd>NUMERIC(12,4) : 12 chiffres dont 4 derriere la virgule
 * <dt>D
 * <dd>DATETIME
 * <dt>A et types inconnus
 * <dd>VARCHAR
 * </dl>
 * @author hkaradimas
 *
 */
public class TableDdl {
  static Logger lg = LogManager.getLogger(TableDdl.class);

  /**
   * Constructeur simple
   */
  public TableDdl() {
  }
  
  /**
   * Emettre l'instruction DDL (<code>CREATE TABLE </code> ...) qui correspond à une définition de groupe.
   * Attention les sous-groupes ne seront pas traités.
   * 
   * @param tableName Nom de la table
   * @param gm La métadonnée pour le groupe à émettre
   * @param primaryKeyName Nom de la clé primaire. Ignoré si 'primaryKeyDecl' est défini, sinon ajoute la clause PRIMARY KEY derrière la déclaration de colonne. Non sensible à la casse.
   * @param primaryKeyDecl Si non vide, ajouté au départ en tant que déclaration complète de clé primaire.
   * Permet de contrôler complètement la création de la clé primaire. Après la déclaration de type, on utilise
   * classiquement "PRIMARY KEY", mais si on veut que la clé soit générée automatiquement (avec Postgresql) on
   * peut utiliser "SERIAL PRIMARY KEY". Pour H2 on peut utiliser "AUTO_INCREMENT PRIMARY KEY".
   * @param dateTimeType Le type à utiliser pour datetime. Typiquement DATETIME, mais sur certains systèmes TIMESTAMP est utilisé à la place.
   * @return Le script de déclaration de données
   */
  public String emitDdl(String tableName, FszGroupMeta gm, String primaryKeyName, String primaryKeyDecl, String dateTimeType) 
  {
    if (primaryKeyName == null) primaryKeyName = "";
    StringBuilder sb = new StringBuilder("CREATE TABLE ");
    sb.append(tableName);
    sb.append("(");
    int fieldCount = 0;
    if (primaryKeyDecl != null && primaryKeyDecl.length() > 0) {
      sb.append(primaryKeyDecl);
      fieldCount++;
    }
    Iterator<FszMeta> childrenIter = gm.getChildMetas().iterator();
    while (childrenIter.hasNext()) {
      FszMeta child = childrenIter.next();
      if (!child.isFieldMeta()) {
        lg.debug(" ce n'est pas un champ, ddl ne sera pas genere.");
        continue;
      }
      if (fieldCount > 0) sb.append(", ");
      FszFieldMeta m = (FszFieldMeta) child;
      String name = m.getStdName();
      if (primaryKeyName.equalsIgnoreCase(name) && primaryKeyDecl != null) {
    	  //On ne fait rien, l'instruction de déclaration de clé primaire a déjà été complètement générée.
      }
      else {
          sb.append(name); sb.append(" ");
          String typ = m.getPreferredType();
          int size = m.getSize();
          if (typ.equals("N")) { sb.append("NUMERIC(12, 4)"); } //12 chiffres, dont 4 après la virgule      
          else if (typ.equals("D")) { sb.append(dateTimeType); }
          else {
            //par défaut on met le type VARCHAR pour "A". Si le type n'est pas "A", c'est qu'il y a un problème, on met juste un message d'erreur
            if (! typ.equals("A")) lg.error("Unknown type '"+typ+"'");
            sb.append("VARCHAR("); sb.append(size); sb.append(")");
          }
          //Si c'est la clé primaire on rajoute juste PRIMARY KEY
          if (primaryKeyName.equalsIgnoreCase(name)) {
            sb.append(" PRIMARY KEY");
          }    	  
      }
      fieldCount++;
    }
    sb.append(")");
    return sb.toString();
  }

}
