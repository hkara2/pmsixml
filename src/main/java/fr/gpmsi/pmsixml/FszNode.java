package fr.gpmsi.pmsixml;

import java.io.IOException;
import java.util.List;

/**
 * Classe abstraite parente des noeuds simples et des noeuds de groupe.
 * Pour prendre une analogie avec des fichiers, un noeud simple est un
 * simple fichier, alors qu'un noeud de groupe est un répertoire, qui
 * contient d'autres noeuds simples et d'autres noeuds de groupe.
 * Pour chaque noeud, on peut savoir s'il s'agit d'un noeud simple ({@link #isField()}
 * renvoie true, ou un noeud de groupe ({@link #isFieldGroup()} renvoie true).
 * Cependant, pour des raisons de facilités de traitement, les noeuds de groupe 
 * sont regroupés ensemble dans un noeud de groupe "conteneur" (pour lequel {@link #isContainer()} est true)
 * Ainsi pour un noeud 
 * @author hkaradimas
 *
 */
abstract
public class FszNode
{
  FszGroup parent;
  
  /** constructeur simple */
  public FszNode() {}
    
  /**
   * est-ce un noeud simple
   * @return true si c'est un noeud simple (pas un groupe)
   */
  abstract public boolean isField();
  
  /**
   * Est-ce un noeud de groupe
   * @return true si c'est un groupe
   */
  abstract public boolean isFieldGroup();
  
  /** 
   * synonyme de {@link #isFieldGroup()}
   * @return true si c'est un groupe
   */
  public boolean isGroup() { return isFieldGroup(); }
  
  /** 
   * est-ce un noeud "conteneur", qui contient des noeuds de groupe de même nom
   * @return true si c'est un conteneur
   */
  abstract public boolean isContainer();
  
  /**
   * Retourner la métadonnées qui définit ce noeud
   * @return la métadonnée qui définit ce noeud.
   */
  abstract public FszMeta getMeta();
  
  /** nombre d'enfantsDefChamp 
   * @return le nombre d'enfantsDefChamp
   * */
  abstract public int getChildCount();
  
  /** liste avec les noeuds enfantsDefChamp 
   * @return une liste avec les noeuds enfant
   * */
  abstract public List<FszNode> getChildren();
  
  /** 
   * Lire l'entrée, en utilisant la stratégie appropriée de lecture de 
   * champs (ex : RSS1, RSA1, ...) 
   * @param in La source d'entrée
   * @throws FieldParseException Si erreur d'analyse
   */
  abstract public void read(InputString in) throws FieldParseException;
  
  /** Faire un dump de ce noeud
   * @param sb le buffer dans lequel faire le dump
   * @param level le niveau d'identation 
   */
  abstract public void dump(StringBuffer sb, int level);
  
  /** Faire un dump de la structure de ce noeud
   * 
   * @param sb le buffer
   * @param level le niveau
   */
  abstract public void dumpStructure(StringBuffer sb, int level);
  
  /**
   * Convertir ce noeud en représentation texte fixe (attention ne convertit pas les sous-groupes)
   * @param sb le {@link StringBuffer} dans lequel mettre le résultat
   * @throws FieldSizeException si la valeur est trop grande pour le champ 
   */
  abstract public void toText(StringBuffer sb) throws FieldSizeException;
  
  /**
   * Crée un nouveau StringBuffer et appelle toText() avec
   * @see #toText(StringBuffer)
   * @return le résultat de l'appel
   * @throws FieldSizeException Si la valeur est trop grande pour entrer dans le champ
   */
  public String toTextString() throws FieldSizeException {
    StringBuffer sb = new StringBuffer();
    toText(sb);
    return sb.toString();
  }
  
  /**
   * Envoie un listing technique ("dump") vers le stringbuffer fourni
   * @param sb Le StringBuffer dans lequel écrire
   */
  public void dump(StringBuffer sb) { dump(sb, 0); }
  
  /**
   * Envoie un dump de ce noeud.
   * @return un dump sous forme de String
   * @throws FieldSizeException Si erreur de taille du champ
   */
  public String dumpString() throws FieldSizeException {
    StringBuffer sb = new StringBuffer();
    dump(sb);
    return sb.toString();
  }
  
  /**
   * Emet un listing technique ("dump") de la structure de ce noeud.
   * @param sb Le StringBuffer dans lequel écrire
   */
  public void dumpStructure(StringBuffer sb) { dumpStructure(sb, 0); }
  
  /**
   * Emet un dump de la structure sous forme de String
   * @return Le dump de structure
   * @throws FieldSizeException Si erreur de taille de champ
   */
  public String dumpStructureString() throws FieldSizeException {
    StringBuffer sb = new StringBuffer();
    dumpStructure(sb);
    return sb.toString();
  }
  
  /**
   * Emettre le XML pour ce noeud, et ses noeuds enfantsDefChamp, récursivement.
   * (Cette méthode a été le but initial de la création de cette librairie, d'où le nom de pmsi<i>xml</i>)
   * L'émission XML se fait de manière très directe, et notamment sans faire de traduction pour 
   * des caractères d'échappement ; pour l'instant dans le PMSI on n'envoie pas les caractère
   * spéciaux '<' et '&' dans les valeurs donc ça devrait aller. 
   * 
   * @param sb Le string buffer dans lequel écrire le XML
   * @param level Le niveau dans lequel ce noeud se trouve (commence à 0)
   * @param levelStr Le texte qui sera envoyé 'level' fois avant le contenu (utilisé pour l'indentation)
   * @param pastEndStr Le texte qui sera envoyé à la fin (soit chaîne vide, soit retour chariot de la plateforme)
   * @throws IOException
   */
  void emitXml(StringBuffer sb, int level, String levelStr, String pastEndStr) 
  {
    String name = getMeta().getStdName();
    if (isContainer()) name = "G_" + name;
    for (int i = 0; i < level; i++) sb.append(levelStr);
    sb.append("<"); sb.append(name); sb.append(">"); 
    if (isField()) {
        FszField fld = (FszField) this;
        sb.append(fld.getValue());
    }
    else if (isFieldGroup()) {        
      sb.append(pastEndStr);
      List<FszNode> children = getChildren();
      for (FszNode child : children) {
        child.emitXml(sb, level+1, levelStr, pastEndStr);
      }
    }
    for (int i = 0; i < level; i++) sb.append(levelStr);
    sb.append("</"); sb.append(name); sb.append(">"); sb.append(pastEndStr);
  }
  
  /**
   * Envoyer le XML, avec indentation de 2 espaces par niveau, avec le
   * retour chariot de la plateforme
   * @param sb Le StringBuffer dans lequel on écrit
   */
  void emitXmlIndented(StringBuffer sb)
  {
    emitXml(sb, 0, "  ", System.getProperty("line.separator"));
  }
  
  /**
   * Envoyer le XML, non indenté (tout sur la même ligne)
   * @param sb Le StringBuffer dans lequel on écrit
   */
  void emitXml(StringBuffer sb)
  {
    emitXml(sb, 0, "", "");
  }

  /**
   * Retourner le parent de ce noeud
   * @return Le parent
   */
  public FszGroup getParent() {
      return parent;
  }

  /**
   * Attribuer le parent de ce noeud.
   * @param parent Le parent
   */
  public void setParent(FszGroup parent) {
      this.parent = parent;
  }
  
  /**
    * Est-ce que la valeur est vide ?
    * La valeur est vide si elle est null ou bien si elle ne contient que des espaces.
    * @see String#trim()
    * @param value La chaîne à tester
    * @return true si la valeur est vide
    */
  public static final boolean isEmpty(String value) { 
    return value == null || value.trim().length() == 0; 
  }

}
