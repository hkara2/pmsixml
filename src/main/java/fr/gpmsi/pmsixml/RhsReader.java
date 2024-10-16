package fr.gpmsi.pmsixml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

/**
 * Lecteur de RHS.
 * Configurer l'objet lecteur en lui indiquant quel répertoire il doit utiliser,
 * à l'aide de la méthode {@link #setMetasDir(File)}.
 * Lancer ensuite la lecture à l'aide d'une des méthodes de lecture :
 * <ul>
 * <li>{@link #readOne(String)}
 * <li>{@link #readOne(String, boolean)}
 * <li>{@link #readOne(String, int, boolean)}
 * </ul>
 * 
 * Avant lancement
 * @author hk
 *
 */
public class RhsReader
{
  static Logger lg = LogManager.getLogger(RhsReader.class);
  
  String[] supportedVersions = {"M09", "M0A", "M19", "M1A"}; //purement informatif
  
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  File metasDir;

  int lineNr = -1;
  
  /**
   * Constructeur
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public RhsReader()
      throws FieldParseException, IOException, MissingMetafileException 
  {
    for (String string : supportedVersions) {
      loadMeta(string);
    }
  }

  private FszGroupMeta loadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    FszGroupMeta meta = new FszGroupMeta(name);
    MetaFileLoader ldr = new MetaFileLoader(metasDir);
    String resourceName = "/rhs"+name.toLowerCase()+".csv";
    InputStream ins = ldr.getInputStream(resourceName);
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
  
  private FszGroupMeta getOrLoadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException
  {
    FszGroupMeta meta = metasByName.get(name);
    if (meta != null) return meta;
    return loadMeta(name);
  }
  
  /**
   * Appelle {@link #readOne(String, int, boolean)} avec rhs, -1, acceptTruncated
   * @param rhs La ligne qui contient le RHS à lire
   * @param acceptTruncated idem
   * @return idem
   * @throws FieldParseException idem
   * @throws IOException idem
   * @throws MissingMetafileException idem
   */
  public FszNode readOne(String rhs, boolean acceptTruncated) 
      throws FieldParseException, IOException, MissingMetafileException
  {
    return readOne(rhs, -1, acceptTruncated);
  }
  
  /**
   * Appelle {@link #readOne(String, int, boolean)} avec rhs, -1, false.
   * @param rhs Le RHS à lire
   * @return Un noeud de groupe qui contient le RHS.
   * @throws FieldParseException idem
   * @throws IOException idem
   * @throws MissingMetafileException idem
   */
  public FszNode readOne(String rhs) 
  		throws FieldParseException, IOException, MissingMetafileException
  {
  	return readOne(rhs, -1, false);
  }
  
  /**
   * Lecture d'un RHS (une ligne qui contient un RHS).
   * @param rhs La ligne qui contient le RHS
   * @param lineNr Le numéro de la ligne RHS
   * @param acceptTruncated Est-ce que l'on accepte des lignes tronquées (qui n'ont pas la longueur définie dans les métadonnées) ?
   * @return Un noeud de groupe qui contient le RHS
   * @throws FieldParseException Si il y a eu une erreur lors de l'analyse du GHS
   * @throws IOException Si il y a eu une erreur d'E/S
   * @throws MissingMetafileException Si le fichier de métadonnées requis par le RHS n'a pas été trouvé, ni dans le
   * répertoire des métadonnées, ni dans les fichiers resource.
   */
  public FszNode readOne(String rhs, int lineNr, boolean acceptTruncated)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    String ver; //version
    this.lineNr = lineNr;
    try {
      //Tentative pour déterminer la version, pas au même endroit selon qu'on est en RHS groupe ou non
      char m1 = rhs.charAt(10);
      char m2 = rhs.charAt(22);
      if (m1 == 'M' && m2 == 'M') {
        ver = rhs.substring(10, 13);
      }
      else {
        ver = rhs.substring(9, 12); //hack pour avoir la version [10;12] dans la spec du RHS non groupe
      }
      lg.debug("ver:"+ver);
      FszGroupMeta meta = getOrLoadMeta(ver);
      lg.debug("Loaded meta:"+meta);
      FszGroupMeta rhsMeta = meta.findChildGroupMeta("RHS"); 
      if (rhsMeta == null) {
          throw new FieldParseException("Pas de meta information retrouvee pour 'RHS'");
      }
      FszNode nd = rhsMeta.makeNewNode();
      //HashMap<String, Integer> countersByGroupName = new HashMap<String, Integer>();
      InputString in = new InputString(rhs);
      in.lineNumber = lineNr;
      in.acceptTruncated = acceptTruncated;
      nd.read(in);
      //nd.readTwoLevels(in, countersByGroupName);
      return nd;
    }
    catch (FieldParseException fpex) {
      fpex.setLineNr(lineNr);
      throw fpex;
    }
  }
  
  /**
   * laissé pour raisons futures
   * @param args _
   * @throws Exception _
   */
  public static void main(String[] args)
      throws Exception
  {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
    //Logger.getRootLogger().setLevel(Level.DEBUG);
  }

  /**
   * Retourner le répertoire des fichiers de métadonnées
   * @return le répertoire
   */
  public File getMetasDir() {
    return metasDir;
  }

  /**
   * Définir le répertoire des métadonnées
   * @param metasDir Le répertoire
   */
  public void setMetasDir(File metasDir) {
    this.metasDir = metasDir;
  }

  /**
   * Retourner le numéro de ligne
   * @return Le numéro de ligne
   */
  public int getLineNr() {
    return lineNr;
  }

  /**
   * Définir le numéro de ligne qui contient ce RHS. Permet en cas de problème de rapporter le numéro de ligne.
   * @param lineNr Le numéro de ligne
   */
  public void setLineNr(int lineNr) {
    this.lineNr = lineNr;
  }

}
