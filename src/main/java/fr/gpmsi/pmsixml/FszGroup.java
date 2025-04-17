package fr.gpmsi.pmsixml;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Groupe de champs qui sont tous au même niveaux.
 * Les groupes peuvent contenir d'autres groupes.
 * Il y a des champs qui servent de compteurs, pour indiquer combien il y a de sous groupes.
 * Par exemple dans un RSS, NDA indique combien il y a de DA, NZA indique combien il y a de ZA.
 * La table qui contient ces compteurs est placée dans le groupe. Ainsi pour un RSA :
 * <pre>
 * [RSA
 * NAG
 * NSR
 * NRU
 *   [AG]
 *   [SR]
 *   [RU
 *   NDA
 *   NZA 
 *     [DA]
 *     [ZA]
 *   ]
 * ]  
 * </pre>
 * Lorsqu'un groupe contient une liste de sous-groupes, ces sous-groupes sont dans un objet FszGroup qui
 * sert de conteneur. Par exemple si on a un RSS qui contient deux DA (par ex. I10 et I480), il sera représenté de la
 * façon suivante :
 * <ul>
 *   <li> FszGroup "RSS"
 *   <ul>
 *     <li> FszGroup "DA" (container == true)
 *     <ul>
 *       <li> FszGroup "DA"
 *       <ul>
 *         <li> FszField TDA = "I10"
 *       </ul>
 *       <li> FszGroup "DA"
 *       <ul>
 *         <li> FszField TDA = "I480"
 *       </ul>
 *     </ul>
 *   </ul>
 * </ul>
 * 
 * @author hkaradimas
 *
 */
public class FszGroup
extends FszNode
{
  static Logger lg = LogManager.getLogger(FszGroup.class);
  
  FszGroupMeta meta;
  ArrayList<FszNode> children = new ArrayList<FszNode>();
  HashMap<String, FszNode> childrenByName = new HashMap<>();
  //Valeurs des compteurs par nom de compteur
  HashMap<String, Integer> countersByName = new HashMap<String, Integer>();
  //Noms des champs de compteur par nom de compteur (NDA pour DA, NCSA pour ACS)
  HashMap<String, String> counterFieldsByName = new HashMap<>();
  
  boolean container;
  
  /**
   * Crée un nouveau groupe qui aura les métadonnées fournies
   * @param meta Les métadonnées de définition du nouveau groupe
   */
  public FszGroup(FszGroupMeta meta) { this.meta = meta; }

  /**
   * Est-ce un champ simple : non ici
   * @return false
   */
  @Override
  public boolean isField() { return false; }

  /**
   * Est-ce un groupe de champs : oui ici
   * @return true
   */
  @Override
  public boolean isFieldGroup() { return true; }

  /** @return l'objet FszMeta représentant les métadonnées de ce groupe */
  @Override
  public FszMeta getMeta() { return meta; }

  @Override
  public int getChildCount() { return children.size(); }

  @Override
  public List<FszNode> getChildren() { return children; }
  
  /**
   * Ajouter un noeud enfant
   * @param child Le noeud enfant
   */
  public void addChild(FszNode child) {
    children.add(child); 
	String name = child.getMeta().getStdName();
	childrenByName.put(name, child);
	child.parent = this;
  }

  /**
   * Enlever tous les enfantsDefChamp
   */
  public void removeChildren() {
  	children.clear(); childrenByName.clear();
  }
  
  /**
   * Lire les feuilles, à partir de la chaîne d'entrée
   * @param in La chaine d'entrée
   * @throws FieldParseException Si erreur d'analyse
   */
  public void readLeafs(InputString in)
      throws FieldParseException
  {
    readLeafs(in, countersByName, counterFieldsByName);
  }
  
  /**
   * Lire toutes les feuilles (pas les sous-groupes) et attribuer les valeurs aux compteurs,
   * dans la map repetionCountByName.
   * @param in La chaîne d'entrée
   * @param repetitionCountByName _
   * @param repetitionFieldNamesByName _
   * @throws FieldParseException _
   */
  public void readLeafs(InputString in, Map<String, Integer> repetitionCountByName, Map<String, String> repetitionFieldNamesByName)
      throws FieldParseException
  {
    FszGroupMeta gmeta = meta;
    for (FszMeta meta : gmeta.getChildMetas()) {
      if (meta.isFieldMeta()) {
        FszFieldMeta fm = (FszFieldMeta) meta;
        FszField fld = (FszField) fm.makeNewNode();
        addChild(fld); //important : permet l'accès aux métadonnées et donc aux compteurs
        fld.read(in); //, repetitionCountByName, repetitionFieldNamesByName);
      }
    }//for
  }
  
  /**
   * Lire tous les sous-groupes d'un type donné, en séquence, en utilisant l'information
   * donnée dans la liste de groupes passée en paramètre.
   * Ex : pour une liste de RU* lire tous les DA* en utilisant le compteur de DA* 
   * dans chaque groupe.
   * @param in La chaîne d'entrée
   * @param sgMeta La métadonnée des sous-groupes que l'on veut
   * @param groups Les groupes à parcourir
   * @return Un groupe conteneur qui contient tous les sous-groupes trouvés
   * @throws FieldParseException _
   */
  public FszGroup readSubGroups__old(InputString in, FszGroupMeta sgMeta, List<FszGroup> groups)
      throws FieldParseException
  {
    Map<String, Integer> repetitionCountByName = new HashMap<String, Integer>();
    Map<String, String> repetitionFieldByName = new HashMap<>();
    FszGroup container = (FszGroup) sgMeta.makeNewNode(); //créer un conteneur pour contenir tous ces sous-groupes
    container.setContainer(true);
    String counterName = sgMeta.getGroupName();
    for (FszGroup g : groups) {
      Integer counter = g.getCounterValue(counterName);
      if (counter == null) {
        lg.error("Pas de compteur retrouve pour '"+counterName+"'");
      }
      else {
        for (int i = 0; i < counter; i++) {
          FszGroup sg = (FszGroup) sgMeta.makeNewNode();
          sg.readLeafs(in, repetitionCountByName, repetitionFieldByName);
          container.addChild(sg);
        }//for
       }
    }//for
    return container;
  }

  /**
   * Lire les groupes enfant depuis le String en entrée.
   * Appelle <code>readSubGroups(is, name, false)</code>
   * @param is le String à partir duquel on veut lire
   * @param name Le nom des groupes à récupérer
   * @return Un groupe conteneur qui contient tous les groupes enfant
   * @throws FieldParseException en cas d'erreur d'analyse
   */
  public FszGroup readSubGroups(InputString is, String name)
      throws FieldParseException
  {
    return readSubGroups(is, name, false);
  }
  
  /**
   * Lire les groupes enfant depuis le String en entrée.
   * 
   * @param is le String à partir duquel on veut lire
   * @param name Le nom des groupes à récupérer et aussi le nom du compteur à utiliser.
   * @param optional True si le sous-groupe est optionnel. S'il n'est pas optionnel, une erreur sera ecrite dans le log indiquant que le meta est manquant.
   * @return Un groupe conteneur qui contient tous les groupes enfant
   * @throws FieldParseException _
   */
  public FszGroup readSubGroups(InputString is, String name, boolean optional)
      throws FieldParseException
  {
    return readSubGroups(is, name, name, optional);
  }
  
  /**
   * Lire les groupes enfant depuis le String en entrée.
   * 
   * @param is le String à partir duquel on veut lire
   * @param name Le nom des groupes à récupérer
   * @param counterName Le nom du compteur à utiliser (parfois ce n'est pas le même nom que le groupe)
   * @param optional True si le sous-groupe est optionnel. S'il n'est pas optionnel, une erreur sera ecrite dans le log indiquant que le meta est manquant.
   * @return Un groupe conteneur qui contient tous les groupes enfant
   * @throws FieldParseException _
   */
  public FszGroup readSubGroups(InputString is, String name, String counterName, boolean optional)
      throws FieldParseException
  {
    FszGroup g;
    FszGroupMeta cgMeta = (FszGroupMeta) meta.getRoot().asGroupMeta().findChildMeta(name);
    if (cgMeta == null) {
      if (!optional) lg.error(" meta non retrouve pour groupe '"+name+"'");
      return null;
    }
    g = new FszGroup(cgMeta); //Créer un conteneur pour contenir tous ces enfantsDefChamp identiques
    g.setContainer(true);
    Integer counter = getCounterValue(counterName); //récupérer la valeur du compteur, qui doit avoir été lue dans les champs précédente et mis dans la Map
    if (counter == null) {
      if (!optional) lg.error("Compteur non trouve pour "+counterName);
      return g;
    }
    //lg.debug("Compteur "+name+" : " + counter);
    for (int i = 0; i < counter; i++) {
      FszGroup sg = (FszGroup) cgMeta.makeNewNode();
      sg.readLeafs(is);
      g.addChild(sg);
    }
    return g;
  }

  /**
   * Faire un "dump" de ce groupe, pour l'afficher par exemple dans un journal d'exécution.
   */
  public void dump(StringBuffer sb, int level) {
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append("("+meta.getStdName()+"(+"+level+"\n");
    for (FszNode child : children) {
      child.dump(sb, level+1);
    }
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append(")"+meta.getStdName()+")+"+level+"\n");
  }
  
  /**
   * Faire un "dump", mais juste de la structure, pour vérifier l'imbrication.
   */
  @Override
  public void dumpStructure(StringBuffer sb, int level) {
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append(meta.getStdName());
    if (container) sb.append("#"); //indique que c'est un conteneur
    sb.append("\n");
    for (FszNode child : children) {
      child.dumpStructure(sb, level+1);
    }
  }
  
  @Override
  public String toString() {
    return "group "+meta.groupName+", "+getChildCount()+" child"+plurEn(getChildCount());
  }

  //Utilisé pour mettre child au singulier ou au pluriel (children)
  private String plurEn(int count) { return count == 1 ? "" : "ren"; }
  
  public boolean isContainer() {
    return container;
  }

  /**
   * Définir si ce groupe est un conteneur
   * @param container true si c'est un conteneur
   */
  public void setContainer(boolean container) {
    this.container = container;
  }
  
  /**
   * Retourner la valeur du compteur
   * @param counterName Le nom du compteur
   * @return La valeur du compteur
   */
  public Integer getCounterValue(String counterName) {
    Integer cv = countersByName.get(counterName);
    return cv;
  }
  
  /**
   * Définir la valeur du compteur
   * @param counterName Le nom du compteur
   * @param value La valeur du compteur
   */
  public void setCounterValue(String counterName, Integer value) {
    countersByName.put(counterName, value);
    FszField counterField = getCounterField(counterName);
    if (counterField != null) {
      if (value == null) counterField.setValue("");
      else counterField.setValue(String.valueOf(value));
    }
  }

  /**
   * Retourner le champ qui contient le compteur 
   * @param counterName Le nom du compteur qui correspond au champ. Par ex. pour RHS, le compteur ACS est contenu dans le champ NCSA de RHS. 
   * @return le champ compteur ou null si non trouve
   */
  public FszField getCounterField(String counterName) {
    String counterFieldName = counterFieldsByName.get(counterName);
    if (counterFieldName == null) {
      System.err.println("Erreur : ne trouve pas de champ compteur pour '" + counterName + "'");
      return null;
    }
    FszField counterField = getChildField(counterFieldName);
    if (counterField == null) {
      System.err.println("Erreur : ne trouve pas de champ '" + counterFieldName + "'");
      return null;
    }
    return counterField;
  }
  
  /**
   * Pour les champs de compteur, les mettre à jour avec les noms de groupe correspondants,
   * récursivement. A appeler avant de générer le texte du FszGroup.
   */
  public void updateCounters() {
    Set<String> counterNames = countersByName.keySet(); //recuperer la liste des compteurs (par ex. pour RHS, ce seront DA, ACS, ACC)
    for (String counterName:counterNames) {      
      FszGroup g = getChildGroup(counterName); //récupérer le groupe conteneur
      setCounterValue(counterName, g.getChildCount()); //mettre à jour la valeur du compteur
      for (FszNode cn:g.getChildren()) {
        //mettre à jour les compteurs de tous les groupes enfantsDefChamp. Par ex. pour RSA, pour le groupe des RU, mettra à jour les compteurs DA et ZA de chaque groupe RU
        if (cn.isFieldGroup()) ((FszGroup)cn).updateCounters();
      }//for
    }//for
  }
  
  /**
   * Retourner une Map avec la liste des compteurs de champs. Ces compteurs sont utilisés pour
   * indiquer le nombre d'éléments que doit avoir le groupe, par exemple pour un groupe RUM on aura un
   * compteur NZA qui indique combien il y a de groupes enfantsDefChamp ZA.
   * @return La Map des compteurs par Nom
   */
  public Map<String, Integer> getCountersByName() { return countersByName; }
  
  /**
   * Retourner une Map avec la liste des noms des champs de compteur par nom de compteur 
   * @return La Map
   */
  public Map<String, String> getCounterFieldsByName() { return counterFieldsByName; }
  
  /**
   * Retourner le noeud enfant nommé
   * @param name Le nom du noeud enfant à retourner
   * @return Le noeud enfant
   */
  public FszNode getChild(String name) { return childrenByName.get(name); }
  
  /**
   * Retourner le groupe enfant nommé
   * @param name Le nom du groupe
   * @return Le groupe enfant
   */
  public FszGroup getChildGroup(String name) { return (FszGroup) getChild(name); }
  
  /**
   * Retourner le champ enfant nommé
   * @param name Le nom du champ à retourner
   * @return Le champ enfant
   */
  public FszField getChildField(String name) { return (FszField) getChild(name); }
  
  /**
   * Renvoie un noeud enfant, en le créant si nécessaire.
   * @param name Le nom du noeud enfant, doit exister dans les métadonnées ou bien null sera retourné
   * @param createIfMissing si true, un noeud enfant sera créé s'il n'y en a pas
   * @return le noeud enfant, ou null si aucune définition n'a été trouvé avec ce noeud dans les métadonnées
   */
  public FszNode getChild(String name, boolean createIfMissing) {
    FszNode nd = childrenByName.get(name);
    if (nd != null) return nd;
    if (!createIfMissing) return null;
    FszMeta cm =  meta.findChildMeta(name);
    if (cm == null) {
      //c'est peut être un groupe, aller à la racine, et rechercher un groupe avec ce nom
      cm = meta.getRoot().asGroupMeta().findChildGroupMeta(name);
      if (cm == null) {
        lg.debug("No child found for "+name+", returning null");
        return null;
      }
      //fall through
    }
    nd = cm.makeNewNode();
    addChild(nd);
    return nd;
  }

  /**
   * Colliger les enfantsDefChamp qui ont un nom donné, récursivement. Ne prendre que les
   * enfantsDefChamp qui ne sont pas des conteneurs.
   * @param name Le nom des enfantsDefChamp que l'on recherche
   * @param lst La liste dans laquelle mettre les résultats
   */
  public void collectChildren(String name, List<FszNode> lst) {
  	for (FszNode child : children) {
			if (child.getMeta().getStdName().equals(name)) {
				if (child.isField()) lst.add(child);
				else {
					if (!((FszGroup)child).isContainer()) lst.add(child);
				}
			}
			if (child.isFieldGroup()) {
				((FszGroup)child).collectChildren(name, lst);
			}
		}//for
  }

  /**
   * Emettre tous les champs enfant en texte fixe, mais sans émettre les sous-groupes.
   */
	@Override
	public void toText(StringBuffer sb)
			throws FieldSizeException 
	{
    	for (FszNode child : children) {
    		if (child.isField()) child.toText(sb);
    	}
	}

	/**
	 * Emettre sous forme textuelle, dans l'ordre naturel. Bien pour débogage, mais attention
	 * lorsqu'on l'utilise pour générer des enregistrements, vérifier le bon ordre des sous-groupes.
	 * @param sb Le buffer à utiliser
	 * @throws FieldSizeException Si il y a un débordement dans la taille d'un champ
	 */
	public void toTextRecursive(StringBuffer sb)
			throws FieldSizeException
	{
    	for (FszNode child : children) {
    		if (child.isField()) child.toText(sb);
    		else ((FszGroup)child).toTextRecursive(sb);
    	}
	}
	
	/**
	 * Crée un StringBuffer, appelle {@link #toTextRecursive(StringBuffer)} et renvoie le résultat
	 * sous forme de String
	 * @return le résultat de {@link #toTextRecursive(StringBuffer)}
	 * @throws FieldSizeException Si il y a un débordement dans la taille d'un champ
	 */
	public String toTextRecursiveString()
	    throws FieldSizeException
	{
	  StringBuffer sb = new StringBuffer();
	  toTextRecursive(sb);
	  return sb.toString();	      
	}
	
	@Override
	public void read(InputString in) 
			throws FieldParseException 
	{
		FszNodeReadStrategy strategy = getMeta().getReadStrategy();
		if (strategy == null) {
			throw new FieldParseException("Pas de strategie de lecture declaree pour '"+getMeta().getStdName()+"'");
		}
		strategy.readNode(in, this);
	}
  
	/**
	 * Créer un PreparedStatement pour l'insertion de ce group en un
	 * nouvel enregistrement.
	 * La base de données doit supporter la génération automatique
	 * de la clé primaire.
	 * @param cxn La connexion
	 * @param tableName Le nom de la table
	 * @return Le nouveau Prepared Statement
	 * @throws SQLException Si il y a une erreur de syntaxe ou d'exécution de la base
	 * @throws ParseException Si l'analyse du champ a détecté une erreur
	 */
	public PreparedStatement makeInsertPs(Connection cxn, String tableName) 
	    throws SQLException, ParseException 
	{
	  StringBuilder sb = new StringBuilder("INSERT INTO ");
	  sb.append(tableName);
	  sb.append("(");
	  int fc = 0;
	  Iterator<FszNode> childrenIter = getChildren().iterator();
	  while (childrenIter.hasNext()) {
	    FszNode nd = childrenIter.next();
	    if (!nd.isField()) continue;
	    FszFieldMeta nm = (FszFieldMeta) nd.getMeta();
	    String name = nm.getStdName();
	    if (fc > 0) sb.append(',');
	    sb.append(name);
	    fc++;
	  }//while
      sb.append(") VALUES (");
      fc = 0;
      childrenIter = getChildren().iterator();
      while (childrenIter.hasNext()) {
        FszNode nd = childrenIter.next();
        if (!nd.isField()) continue;  //not a field, we ignore it
        //FszField fnd = (FszField) nd;
        if (fc > 0) sb.append(',');
        //if (fnd.isEmpty()) sb.append("NULL");
        //else sb.append('?');
        sb.append('?'); //we handle nulls via setNull in fillInsertPs method. This is the only way to deal with batch inserts
        fc++;
      }
      sb.append(")");	  
      PreparedStatement ps = cxn.prepareStatement(sb.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
      return ps;
	}
	
	/**
	 * Remplir le PreparedStatement qui est destiné à l'insertion avec les
	 * valeurs contenues dans ce groupe
	 * @param ps Le PreparedStatement à remplir
	 * @throws SQLException Si erreur d'exécution dans la base ou erreur de syntaxe du SQL
	 * @throws ParseException Si il y a une erreur lors de l'analyse du groupe
	 */
	public void fillInsertPs(PreparedStatement ps)
	    throws SQLException, ParseException
	{
      int ix = 1; //for jdbc indexes, we start at 1
	  Iterator<FszNode> childrenIter = getChildren().iterator();
      while (childrenIter.hasNext()) {
        Object oval = null;
        FszNode nd = childrenIter.next();
        if (!nd.isField()) continue; //not a field, we ignore it
        FszField fnd = (FszField) nd;
        //if (fnd.isEmpty()) continue; //skip empty fields as NULL was generated for them
        FszFieldMeta nm = (FszFieldMeta) nd.getMeta();
        String typ = nm.getPreferredType();
        if (typ.equals("N")) {
          BigDecimal bd = fnd.getValueAsBigDecimal();
          if (bd == null) ps.setNull(ix, Types.NUMERIC);
          else {
            //240904 hk ici on convertit la valeur dans sa vraie valeur, donc si c'est un nombre au
            //format 1+4 , 10654 deviendra 1.0654
            //bien y penser lors de la déclaration des types, ainsi que lors du traitement des résultats !
            bd = fnd.getCorrectedValue();
            ps.setBigDecimal(ix, bd);
          }
          oval = bd;
        }
        else if (typ.equals("D")) {
          Date dt = fnd.getValueAsEuropeanDate();
          Timestamp ts = null;
          if (dt == null) ps.setNull(ix, Types.TIMESTAMP);
          else { ts = new Timestamp(dt.getTime()); ps.setTimestamp(ix, ts); }
          oval = ts;
        }
        else {
          String str = fnd.getValue().trim();
          if (fnd.isEmpty()) ps.setNull(ix, Types.VARCHAR); 
          else ps.setString(ix, str);
          oval = str;
        }
        lg.debug(ix + " ("+nm.getStdName()+") was set to '" + fnd.value + "' ("+oval+")");
        ix++;
      }
      lg.debug("All fields of "+getMeta().getStdName()+" filled.");
	}
	
	/**
	 * Copier les champs enfantsDefChamp dans le groupe destination (qui peut avoir une autre version des métadonnées).
	 * Recopie récursivement les sous-groupes.
	 * Cette méthodes est utile pour passer d'une version à une autre (typiquement pour les RSS)
	 * @param dst Destination
	 */
	public void copyFieldsTo(FszGroup dst) {
	  FszGroupMeta dstMeta = dst.meta;
	  List<FszMeta> dstChildMetas = dstMeta.getChildMetas();
	  for (FszMeta meta : dstChildMetas) {
        String stdName = meta.getStdName();
        if (meta.isFieldMeta()) {
          FszField dstChildNode = (FszField) dst.getChild(stdName, true); //ajouter le champ dans tous les cas s'il n'y est pas
          //essayer de recuperer le champ depuis la source
          lg.debug("Recherche "+stdName);
          FszNode srcChild = getChild(stdName);
          if (srcChild != null && srcChild.isField()) {
            dstChildNode.setValue(((FszField)srcChild).getValue()); 
          }
          else {
            lg.debug("Champ '"+stdName+"' non trouve dans la source");
          }
          //dst.addChild(dstChildNode);
        }
      }
      List<FszNode> srcChildren = getChildren();
	  for (FszNode srcChild:srcChildren) {
	    String stdName = srcChild.getMeta().getStdName();
        FszNode dstChild = dst.getChild(stdName, true);
        if (dstChild == null) {
          lg.debug("Child '"+stdName+"' not found, no copy of branch.");
          continue; //pas d'équivalent dans la destination, on ignore et passe au suivant
        }
        if (dstChild.isField()) {
          //on ne fait rien sur les champs
        }
        else {
          //c'est un groupe
          FszGroup srcGroup = (FszGroup) srcChild;
          FszGroup dstGroup = (FszGroup) dstChild;
          if (srcGroup.isContainer()) {
          //Pour les conteneurs, on refait un traitement séparé sur chaque enfant pour conserver les métadonnées
            dstGroup.setContainer(true);   
            FszGroupMeta dstGroupMeta = (FszGroupMeta) dstGroup.getMeta();
            for (FszNode srcGroupChild : srcGroup.getChildren()) {              
              FszGroup dstGroupChild = dstGroupMeta.makeBlankInstance();
              dstGroup.addChild(dstGroupChild);
              ((FszGroup)srcGroupChild).copyFieldsTo(dstGroupChild);
            }
          }
          else {
            //simple appel récursif
            srcGroup.copyFieldsTo(dstGroup);
          }
        }//if (dstChild.isField())
      }//for
	}
	  
}
