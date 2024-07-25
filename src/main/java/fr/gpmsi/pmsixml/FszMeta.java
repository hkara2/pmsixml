package fr.gpmsi.pmsixml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe abstraite implémentée par les objets de métadonnées de champs.
 * Les objets qui héritent de cette classe implémentents et/ou spécialisent les méthodes 
 * selon leur but.
 * @author hkaradimas
 *
 */
abstract
public class FszMeta
{
  static Logger lg = LogManager.getLogger(FszMeta.class);
  
  public static final String PREFIX = "fr.gpmsi.pmsixml"; 
  public static final String PREFIX_DIR = "fr/gpmsi/pmsixml"; 
	
  private static boolean loadDebuggingEnabled = false;
  	
  private FszNodeReadStrategy readStrategy;
  
  /**
   * Est-ce une métadonnée pour un simple champ
   * @return true si c'est une métadonnée pour un champ
   */
  abstract
  public boolean isFieldMeta();
  
  /** Est-ce une métadonnée pour un groupe (qui peut contenir des champs et d'autres groupes)
   * @return true si c'est une métadonnée pour un groupe
   */
  abstract
  public boolean isGroupMeta();
  
  /** Retourner le nom "standard", qui peut être utilisé pour nommer un champ XML ou une colonne de base de données
   * 
   * @return le nom standard
   */
  abstract
  public String getStdName();

  /** Faire un "dump" de cette métadonnées (par ex. pour un journal d'exécution)
   * 
   * @param sb le buffer dans lequel faire le dump
   */
  abstract
  public void dump(StringBuffer sb);
  
  /** Faire un "dump" mais avec un niveau d'espacement particulier
   * 
   * @param sb le buffer dans lequel faire le dump
   * @param level le niveau d'indentation
   */
  abstract
  public void dump(StringBuffer sb, int level);
  
  /** Créer un nouveau noeud vide qui correspond à ces métadonnées
   * 
   * @return le noeud créé
   */
  abstract
  public FszNode makeNewNode();
  
  /** Retourner le noeud métadonnées parent
   * 
   * @return le noeud parent
   */
  abstract
  public FszMeta getParent();
  
  /**
   * attribuer le noeud parent
   * @param parent le noeud parent
   */
  abstract
  public void setParent(FszMeta parent);
  
  /** Retourner la stratégie de lecture (par exemple MONO, ou RSS1, etc.) 
   * @return la stratégie à utiliser pour la lecture du texte
   * */
  public FszNodeReadStrategy getReadStrategy() {
  	if (readStrategy != null) return readStrategy;
  	FszMeta p = getParent();
  	if (p != null) return p.getReadStrategy(); 
  	else return null; 
  }
  
  /**
   * définir la stratégie de lecture à utiliser
   * @param strategy la stratégie
   */
  public void setReadStrategy(FszNodeReadStrategy strategy) {
  	this.readStrategy = strategy; 
  }
  
  /** Conversion directe en FszGroupMeta
   * 
   * @return cette métadonnée, en tant que FszGroupMeta
   */
  public FszGroupMeta asGroupMeta() { return (FszGroupMeta) this; }
  
  /** Conversion directe en FszFieldMeta
   * 
   * @return cette métadonnée, en tant que FszFieldMeta
   */
  public FszFieldMeta asFieldMeta() { return (FszFieldMeta) this; }
    
  /** Retourner le noeud racine des métadonnées
   * 
   * @return le noeud racine
   */
  public FszMeta getRoot() {
    FszMeta r = this;
    while (r.getParent() != null) r = r.getParent();
    return r;
  }

  /**
   * Le débogage du chargement est-il autorisé ? 
   * Utilisé pour la mise au point de cette classe.
   * @return true si le débogage du chargement est autorisé
   */
  public static boolean isLoadDebuggingEnabled() {
	return loadDebuggingEnabled;
  }

  /**
   * définir si on active le débogage du chargement
   * @param loadDebuggingEnabled mettre à true pour autoriser le débogage
   */
  public static void setLoadDebuggingEnabled(boolean loadDebuggingEnabled) {
	FszMeta.loadDebuggingEnabled = loadDebuggingEnabled;
  }
  
}
