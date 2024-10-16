package fr.gpmsi.pmsixml;

import java.io.IOException;
import java.util.HashMap;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.gpmsi.pmsixml.nx.Nx2Xml;

/**
 * Lecteur de Fixed SiZe Record (enregistrement à champs fixes).
 * Chaque enregistrement à champs fixes est soit "mono-niveau", c'est à dire que
 * c'est une liste de champs, sans sous-champs ni sous-groupes, soit "multi-niveaux", c'est à
 * dire qu'il y a une méthode pour représenter les champs enfantsDefChamp.
 * La stratégie de lecture est donc à adapter selon la représentation des niveaux.
 * Les stratégies possibles sont :
 * <ul>
 * <li>MONO pour les fichiers mono-niveaux (FICHCOMP, VIDHOSP, ...)
 * <li>RHS1 pour les résumés hospitaliers de sortie (RHS)
 * <li>RSA1 pour les résumés de sortie anonymisés (RSA)
 * <li>RSS1 pour les résumés standardisés de sortie (RSS)
 * <li>VH1 pour les fichiers vid hosp
 * <li>NX1 pour les fichiers NX (ne plus utiliser, cf. package fr.gpmsi.pmsixml.nx
 * </ul>
 * Le nom de la stratégie est contenu dans dans le fichier de métadonnées, dans la
 *  ligne dont la première colonne est 
 * "S:", située en général en fin de fichier de métadonnées.
 * Le nom est utilisé pour charger l'objet <code>FszNodeReadStrategy</code> approprié.
 * Exemple de code pour lire un VIDHOSP :
 * <pre>
 *     MonoLevelReader app = new MonoLevelReader();
 *     MetaFileLoader mfl = new MetaFileLoader();
 *     FszReader fszr = new FszReader(mfl, "vidhosp");
 *     String vh = (... lire ligne VIDHOSP ici ...);
 *     InputString instr = new InputString(vh);
 *     FszGroup gn = (FszGroup) fszr.readOne(instr);
 * </pre>
 * @see FszNodeReadStrategyFactory 
 * @see Nx2Xml
 * @author hk
 *
 */
public class FszReader
{
  static Logger lg = LogManager.getLogger(RssReader.class);
    
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  private MetaFileLoader metaLoader;
  
  String metaName;
  
  String descriptor;
  
  FszNodeReadStrategy nrStrategy;
  
  /**
   * Retourner le chargeur de fichiers de métadonnées
   * @return Le {@link MetaFileLoader}
   */
  public MetaFileLoader getMetaLoader() {
    return metaLoader;
  }

  private void setMetaLoader(MetaFileLoader metaLoader) {
    this.metaLoader = metaLoader;
  }

  /**
   * Constructeur
   * @param metaLoader Le chargeur du fichier de métadonnées
   * @param metaName Le nom du fichier des métadonnées
   * @param readStrategyDescriptor Le code descripteur de la stratégie de lecture
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public FszReader(MetaFileLoader metaLoader, String metaName, String readStrategyDescriptor)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    this.setMetaLoader(metaLoader);
    this.metaName = metaName;
    this.descriptor = readStrategyDescriptor;
    nrStrategy = FszNodeReadStrategyFactory.makeFszNodeReadStrategy(descriptor);
  }
  
  /**
   * Constructeur
   * @param metaLoader Le chargeur du fichier de métadonnées
   * @param readStrategyDescriptor Le code descripteur de la stratégie de lecture
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public FszReader(MetaFileLoader metaLoader, String readStrategyDescriptor)
      throws FieldParseException, IOException, MissingMetafileException 
  {
      this(metaLoader, null, readStrategyDescriptor);
  }
  
  /**
   * Lire une structure à champs fixes (une ligne en général).
   * @param in la InputString à utiliser
   * @return Le noeud racine de la structure qui a été lue
   * @throws FieldParseException si une erreur d'analyse est survenue
   * @throws IOException si il y a une erreur d'entrée-sortie
   * @throws MissingMetafileException si une métadonnée n'a pas été trouvée
   */
  public FszNode readOne(InputString in)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    //si pas de nom de méta fichier donné, essayer de le deviner à partir du fichier
    //et de la stratégie (utile notamment pour RSS, RSA, etc. mais ne marche pas pour
    //les fichiers mono niveau comme les FICHCOMP par exemple, là il faut fournir le
    //nom des métadonnées à utiliser
    if (metaName == null) metaName = nrStrategy.readMetaName(in);
    //ajouter si besoin le .csv final et / ou le "/" initial
    if (!metaName.toLowerCase().endsWith(".csv")) metaName = metaName + ".csv";
    if (!metaName.startsWith("/")) metaName = "/"+metaName;
    FszGroupMeta meta = getMetaLoader().getOrLoadMeta(metaName);
    //lg.debug("Loaded meta:"+meta);
    FszGroupMeta rumMeta = meta.getFirstChildGroupMeta();
    if (rumMeta == null) {
    	throw new FieldParseException("Pas de meta information retrouvee pour " + meta);
    }
    FszNode nd = rumMeta.makeNewNode();
    nd.read(in);
    return nd;
  }

  /**
   * Le nom du fichier de métadonnées à utiliser.
   * Peut être vide (par ex. pour les RSS) à ce moment-là c'est la stratégie de lecture qui
   * va essayer de deviner le nom à utiliser en utilisant notamment le numéro de version.
   * @return le nom du fichier de métadonnées à utiliser.
   */
  public String getMetaName() {
    return metaName;
  }

  /**
   * Définir le nom du fichier de métadonnées à utiliser (cf. {@link #getMetaName()}
   * @param metaName Le nom du fichier
   */
  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }
  
}
