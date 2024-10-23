package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Métadonnées d'un groupe d'enregistrements de longeur fixe.
 * Pour charger les métadonnées :
 * Si les définitions de groupe sont disponibles dans les ressources, utiliser
 * getResourceAsStream, à partir de votre classe.
 * Tous les fichiers de ressource sont au format UTF-8.
 * Par exemple avec "MaClasse" :
 * <pre>
 * FszGroupMeta rss016 = new FszGroupMeta("rss016");
 * rss016.parse(new InputStreamReader(MaClasse.class.getResourceAsStream("/rss016.csv"), "UTF-8"));
 * </pre>
 * 
 * Format du fichier csv de métadonnées :
 * 
 * Colonnes et leur signification :
 * 
 * <ol>
 * <li> Abréviation (3 lettres, parfois jusqu'à 6) du groupe de champs
 * <li> Libellé du champ, copié/collé depuis la documentation ATIH
 * <li>	Nom codé, en majuscules, si possible unique, souvent le libellé a été utilisé pour concevoir ce nom codé.
 * Par exemple dans la définition ATIH pour le RUM pour le champ "N° Administratif local de séjour"
 * c'est le nom codé NADL que nous avons défini. Cette définition n'a rien d'officiel, elle n'a rien à voir avec
 * l'ATIH, elle est purement arbitraire. C'est cependant ce nom codé qui va servir à désigner le champ lors des
 * manimulations ultérieures.
 * <li>	Taille du champ, copié/collé depuis la documentation ATIH
 * <li>	Début du champ, commence à 1, copié/collé depuis la documentation ATIH
 * <li>	Fin du champ, copié/collé depuis la documentation ATIH
 * <li>	Obligatoire : O ou N, copié/collé depuis la documentation ATIH
 * <li>	Type : A, N (A = alphanumérique, N = numérique), recopié depuis la documentaiton ATIH
 * <li>	Type préféré : A,N,D . C'est le type final qui va être utilisé. Ici il y a un type supplémentaire, D, 
 * qui signifie que le champ contient une date. Le format de la date devra se trouver dans le champe 13
 * et c'est le plus souvent JJMMAAAA
 * <li>	Cadrage/Remplissage[3], copié/collé depuis la documentation ATIH. Le plus souvent on a pour 
 * le texte : Gauche/Espace , pour les nombres : Droite/Zéro, et pour le reste : NA/NA
 * <li>	Remarques
 * <li>	Compteur
 * <li>	Format

 * </ol>
 * 
 * @author hkaradimas
 *
 */
public class FszGroupMeta
extends FszMeta
{
  static Logger lg = LogManager.getLogger(FszGroupMeta.class);
  static HashMap<String, FszGroupMeta> metasByName = new HashMap<>();
  
  /** Nom de ce groupe de champs (ex : "RSA", "DAD*", etc.) */
  String groupName;
  
  /** 
   * Métadonnées du champ qui contient le nombre de répétitions de ce groupe.
   * Si cela n'a pas lieu d'être, est mis à null. 
   */
  FszFieldMeta counterField;
  
  ArrayList<FszMeta> childMetas = new ArrayList<FszMeta>();
  FszMeta parent;
  
  /**
   * Fonction utilitaire statique pour charger les métadonnées, soit à partir des
   * ressources, soit à partir du répertoire des métadonnées indiqué dans l'appel.
   * Les métadonnées sont stockées dans une table, pour que la recherche ne se
   * fasse pas à chaque fois à nouveau.
   * @param name Le nom simple du fichier de métadonnées à rechercher (ex : 'rss016'), le préfixe /fr/gpmsi/pmsixml/
   * et le suffixe '.csv' sont ajoutés dans cette méthode
   * @param metasDir Le répertoire dans lequel rechercher des fichiers de définitions.
   *     Peut être null. Si non null, la recherche des métadonnées se fera d'abord dans
   *     ce répertoire, puis dans les ressources.
   * @return La métadonnée pour le groupe
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  static public FszGroupMeta loadMeta(String name, File metasDir)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    FszGroupMeta meta = new FszGroupMeta(name);
    MetaFileLoader ldr = new MetaFileLoader(metasDir);
    String resourcePath = "/fr/gpmsi/pmsixml/" + name.toLowerCase()+".csv";
    InputStream ins = ldr.getInputStream(resourcePath);
    Reader rdr = new InputStreamReader(ins, "UTF-8");
    try {
      meta.parse(rdr);
      metasByName.put(name, meta);
      return meta;
    }
    finally {
      rdr.close();
    }
  }  
  
  /**
   * Fonction utilitaire statique, identique à {@link #loadMeta(String, File)} ,
   * sauf que si les métadonnées existent déjà dans la table, elles sont retournées
   * immédiatement.
   * @param name Le nom
   * @param metasDir Le répertoire des métadonnées (peut être null)
   * @return Les métadonnées du groupe
   * @throws FieldParseException Si erreur d'analyse d'un champ de métadonnées
   * @throws IOException Si erreur d'entrée / sortie
   * @throws MissingMetafileException Si métadonnées non retrouvées
   */
  static public FszGroupMeta getOrLoadMeta(String name, File metasDir)
      throws FieldParseException, IOException, MissingMetafileException
  {
    FszGroupMeta meta = metasByName.get(name);
    if (meta != null) return meta;
    return loadMeta(name, metasDir);
  }

  /**
   * Rechercher un fichier de métadonnées, depuis les ressources uniquement.
   * Equivalent à <code>getOrLoadMeta(name, null)</code>
   * 
   * @param name Le nom du fichier de métadonnées à rechercher dans les ressources
   * @return La métadonnée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  static public FszGroupMeta getOrLoadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException
  {
    return getOrLoadMeta(name, null);
  }
  
  /**
   * Constructeur
   * @param name Le nom du groupe
   */
  public FszGroupMeta(String name) {
    this.groupName = name;
  }

  /** @return false ici */
  @Override
  public boolean isFieldMeta() { return false; }
  
  /** @return true ici */
  @Override
  public boolean isGroupMeta() { return true; }
  
  public void dump(StringBuffer sb) { dump(sb, 0); }
  
  public void dump(StringBuffer sb, int level)
  {
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append("<FIELDGROUP NAME=\""+groupName+"\"");
    if (counterField != null) {
      sb.append(" COUNTER=\""+counterField.getStdName()+"\"");
    }
    sb.append(">\n");
    for (FszMeta fm : childMetas) { fm.dump(sb, level+1); }
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append("</FIELDGROUP>\n");    
  }
  
  /**
   * Retourner la liste des métadonnées enfant.
   * @return La liste des métadonnées enfant.
   */
  public List<FszMeta> getChildMetas() { return childMetas; }
  
  /**
   * Ajouter une métadonnée enfant à la liste des métadonnées
   * @param fm Métadonnée à ajouter
   */
  public void addChildMeta(FszMeta fm) { childMetas.add(fm); fm.setParent(this); }

  /**
   * Retourner le nom de ce groupe
   * @return Le nom
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Attribuer le nom du groupe
   * @param groupName Le nom
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  /**
   * Retourner les métadonnée du champ qui contient le nombre de répétitions de ce groupe.
   * Par exemple dans le rss116 le NZA (qui correspond aux colonnes 138 à 140) contient le nombre
   * de zones d'acte.
   * Le groupe ZA se verra donc associer le FszFieldMeta du NZA il saura ainsi quel champ utiliser
   * pour lire son compteur de répétition
   * @return Les métadonnées du champ compteur de répétition
   */
  public FszFieldMeta getCounterField() {
    return counterField;
  }

  /**
   * Donner la définition du champ de compteur
   * @param counterField Le champ de compteur
   */
  public void setCounterField(FszFieldMeta counterField) {
    this.counterField = counterField;
  }

  /**
   * Analyse du fichier et construction de la table des métadonnées
   * Attention ce n'est pas une vraie arborescence, il n'y a que deux niveaux.
   * A chaque changement de niveau, on ne fait que recréer un FszGroupMeta.
   * Donc par exemple après analyse de rsaxxx.csv on a quelque chose comme :
   * <pre>
   * (liste des FszGroupMeta
   *     (FszGroupMeta pour RSA)
   *         FszFieldMeta pour FINESS
   *         FszFieldMeta pour VRSA
   *         etc.
   *     )  
   *     (FszGroupMeta pour AG ...)
   *     (FszGroupMeta pour SR ...)
   *     (FszGroupMeta pour RU ...)
   *     (FszGroupMeta pour DA ...)
   *     (FszGroupMeta pour ZA
   *         FszFieldMeta pour DLDA
   *         FszFieldMeta pour CCCAM
   *         etc.
   *     )
   * )
   * </pre> 
   * La structure arborescente est créée à partir de la stratégie de lecture, qui va décider dans quel
   * ordre lire les champs et comment raccorder parents/enfantsDefChamp.
   * De la même façon la table des compteurs est unique pour toutes les définitions meta.
   * @param rdr Le {@link Reader} à utiliser
   * @throws IOException Si erreur E/S
   * @throws FieldParseException Si erreur d'analyse
   */
  public void parse(Reader rdr)
      throws IOException, FieldParseException
  {
    BufferedReader br = new BufferedReader(rdr);
    String line = br.readLine();
    int nline = 0;
    String lastFgn = "";
    FszGroupMeta fgm = null;
    while (line != null) {
      if (isLoadDebuggingEnabled()) lg.debug("line:"+line);
      if (nline == 0 || line.length() == 0 || line.trim().startsWith("#")) {
        //ignorer la première ligne, ainsi que les lignes vides, et les lignes qui commencent par #
        line = br.readLine(); nline++;
        continue;
      }
      //chaque ligne est une définition de champ (FszFieldMeta)
      FszFieldMeta fm = new FszFieldMeta();
      fm.readFromCsvLine(line);
      String fgn = fm.getFieldGroupName().trim();
      if (fgn.equals("S:")) {
        //le nom de groupe spécial S: sert à déclarer la stratégie de lecture
      	String strategyName = fm.getLongName();
      	FszNodeReadStrategy strategy = FszNodeReadStrategy.findStrategy(strategyName);
      	if (strategy == null) {
      		lg.error("Pas de strategie de lecture trouvee pour '"+strategyName+"'");
      	}
      	setReadStrategy(strategy);
        line = br.readLine(); nline++;
      	continue;
      }
      if (!fgn.equals(lastFgn)) {
        //changement de nom de groupe, on crée un nouveau groupe enfant
        //comme au démarrage le nom précédent est "", ça marche même pour la première ligne
        fgm = new FszGroupMeta(fgn);
        addChildMeta(fgm);
        lastFgn = fgn;
      }
      fgm.addChildMeta(fm); //ajouter le champ au groupe courant
      line = br.readLine(); nline++;
    }
    br.close();
    //Parcourir maintenant les groupes pour récupérer les champs qui sont des compteurs
    HashMap<String, FszFieldMeta> counterFieldsByGroupName = new HashMap<String, FszFieldMeta>();
    for (FszMeta meta : childMetas) {
      if (meta.isFieldMeta()) {
        throw new FieldParseException("FszFieldMeta non autorise ici !"); //erreur interne, ne devrait pas se produire
      }
      FszGroupMeta gmeta = (FszGroupMeta) meta;
      for (FszMeta cmeta : gmeta.getChildMetas()) {
        if (cmeta.isGroupMeta()) {
          throw new FieldParseException("FszGroupMeta non autorise ici !"); //erreur interne, ne devrait pas se produire
        }
        FszFieldMeta fcmeta = (FszFieldMeta) cmeta;
        if (!isEmpty(fcmeta.getFieldCounter())) {
          counterFieldsByGroupName.put(fcmeta.getFieldCounter(), fcmeta);
        }
      }
    }//for
    //parcourir les groupes et leur attribuer leurs champs de compteurs
    for (FszMeta meta : childMetas) {
      if (meta.isFieldMeta()) {
        throw new FieldParseException("FszFieldMeta non autorise ici !");
      }
      FszGroupMeta gmeta = (FszGroupMeta) meta;
      FszFieldMeta counterMeta = counterFieldsByGroupName.get(gmeta.getGroupName());
      if (counterMeta != null) gmeta.setCounterField(counterMeta);
    }    
  }

  private final boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }
  
  @Override
  public String toString() {
  	int sz = getChildMetas().size();
  	String plural = sz == 1 ? "" : "ren";
    return "[Group '"+groupName+"' with "+sz+" child"+plural+"]";
  }

  /**
   * Crée un nouveau FszGroup qui nous a en métadonnées.
   */
  @Override
  public FszNode makeNewNode() {
    FszGroup grp = new FszGroup(this);
    return grp;
  }
  
  /**
   * Rechercher une définition parmi les enfantsDefChamp et sous-enfantsDefChamp, récursivement.
   * @param name Le nom de la définition
   * @return La métadonnée ou null si non trouvé
   */
  public FszMeta findChildMeta(String name) {
    for (FszMeta childMeta : childMetas) {
      if (name.equals(childMeta.getStdName())) return childMeta;
      if (childMeta.isGroupMeta()) {
        FszMeta m2 = ((FszGroupMeta)childMeta).findChildMeta(name); //recherche récursive dans le groupe enfant
        if (m2 != null) return m2;
      }
    }//for
    //not found, return null.
    return null;
  }

  /**
   * Rechercher une définition de sous-groupe.
   * @param name Le nom du sous-groupe à rechercher.
   * @return le sous-groupe ou null si non trouvé
   */
  public FszGroupMeta findChildGroupMeta(String name) {
    for (FszMeta childMeta : childMetas) {
      //S ystem.out.println("Child meta : "+childMeta.getStdName());
      if (name.equals(childMeta.getStdName()) && childMeta.isGroupMeta()) {
      	return (FszGroupMeta) childMeta;
      }
      if (childMeta.isGroupMeta()) {
        FszMeta m2 = ((FszGroupMeta)childMeta).findChildMeta(name);
        if (m2 != null && m2.isGroupMeta()) return (FszGroupMeta) m2;
      }
    }//for
    //not found, return null.
    return null;
  }

  /**
   * Retourner la première métadonnée enfant.
   * Le premier noeud de métadonnées du groupe est le premier noeud utile, par ex "RSS" ou "VH".
   * Cette méthode permet d'y accéder facilement.
   * @return Le premier noeud de métadonnées du groupe
   */
  public FszGroupMeta getFirstChildGroupMeta() {
    for (FszMeta childMeta : childMetas) {
      if (childMeta.isGroupMeta()) {
        return (FszGroupMeta) childMeta;
      }
    }//for
    //non retrouvé, retourner null.
    return null;
  }
  
  @Override
  public String getStdName() {
    return groupName;
  }

  @Override
  public FszMeta getParent() {
    return parent;
  }

  @Override
  public void setParent(FszMeta parent) { this.parent = parent; }
  
  /**
   * Générer une instance vierge, avec tous les champs et groupes peuplés de chaînes vides.
   * Fonctionne comme {@link #makeBlankInstance()} sauf que crée aussi des groupes et sous-groupes.
   * Peut être utile pour générer des nouvelles instances.
   * @return Instance vierge
   */
  public FszGroup makeBlankInstanceWithBlankGroups() {
  	FszGroup g = (FszGroup) makeNewNode();
  	for (FszMeta meta : childMetas) {
			if (meta.isFieldMeta()) {
			    //FszFieldMeta fm = (FszFieldMeta) meta;
				FszField f = (FszField) meta.makeNewNode();
				f.setValue("");
				g.addChild(f);
			}
			else {
			  //it's group meta
			  FszGroup cg = ((FszGroupMeta)meta).makeBlankInstanceWithBlankGroups();
			  g.addChild(cg);
			}
		}//for
  	return g;
  }
  
  /**
   * Créer une instance vierge, mais uniquement pour les champs simples, sans sous-groupes.
   * Crée un FszGroup, et pour chaque childMeta crée un FszField avec la valeur "".
   * C'est la méthode à utiliser de préférence pour la génération de nouveaux groupes.
   * @return L'instance vierge
   */
  public FszGroup makeBlankInstance() {
    FszGroup g = (FszGroup) makeNewNode();
    for (FszMeta meta : childMetas) {
      if (meta.isFieldMeta()) {
          //FszFieldMeta fm = (FszFieldMeta) meta;
          FszField f = (FszField) meta.makeNewNode();
          f.setValue("");
          g.addChild(f);
      }
    }//for
    return g;
  }

  /**
   * Retourner l'index de ce champ simple enfant dans la déclaration (ne compte pas les groupes comme enfant)
   * @param fieldStdName le nom "standard" (par exemple pour un RSS : "DP" ou "NADL")
   * @return L'index du champ
   */
  public int fieldIndexOf(String fieldStdName) {
    int i = 0;
    for (FszMeta childMeta : childMetas) {
      if (!childMeta.isFieldMeta()) continue;
      if (childMeta.getStdName().equals(fieldStdName)) return i;
      i++;
    }//for
    return -1; //non trouvé, retourner -1
  }
}
