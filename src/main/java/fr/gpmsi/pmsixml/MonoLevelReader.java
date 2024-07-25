package fr.gpmsi.pmsixml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lecteur pour des fichiers fsz (à positions fixes) qui n'ont qu'un seul niveau
 * (donc pas de sous-niveau), comme les fichiers d'ATU, de TRA, etc.
 * Il suffit d'instancier un objet MonoLevelReader, puis de définir metasDir et metaName.
 * A noter que pour lire un VIDHOSP il vaut mieux utiliser un FszReader
 * @author hkaradimas
 *
 */
public class MonoLevelReader {
  static Logger lg = LogManager.getLogger(MonoLevelReader.class);
  
  File metasDir;
  String metaName;
  
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  boolean truncatedInputAccepted = true;
  
  public MonoLevelReader() {
  }

  private FszGroupMeta loadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    FszGroupMeta meta = new FszGroupMeta(name);
    String resourceName = name+".csv";
    if (metasDir == null) resourceName = "/"+FszMeta.PREFIX_DIR+"/"+resourceName;
    MetaFileLoader ldr = new MetaFileLoader(metasDir);
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
  
  /**
   * Retourner ou charger les métadonnées
   * @return La métadonnée de groupe
   * @throws FieldParseException Si erreur d'analyse
   * @throws IOException Si erreur d'E/S
   * @throws MissingMetafileException Si métadonnées non trouvées
   */
  public FszGroupMeta getOrLoadMeta()
  		throws FieldParseException, IOException, MissingMetafileException 
  {
  	return getOrLoadMeta(metaName);
  }
  
  private FszGroupMeta getOrLoadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException
  {
    FszGroupMeta meta = metasByName.get(name);
    if (meta != null) return meta;
    return loadMeta(name);
  }
   
  public FszGroup readMonoLevel(String str, int linenr)
      throws FieldParseException, IOException, MissingMetafileException
  {
    InputString is = new InputString(str);
    is.acceptTruncated = truncatedInputAccepted;
    is.lineNumber = linenr;
    return readMonoLevel(is);
  }
  
  /**
   * Lire un fichier mono-niveau
   * @param is Le flux d'entrée
   * @return Le groupe mono-niveau
   * @throws FieldParseException Si erreur d'analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si fichier des métadonnées non trouvé
   */
  public FszGroup readMonoLevel(InputString is)
	      throws FieldParseException, IOException, MissingMetafileException
	  {
	    //get meta
	    lg.debug("meta:"+metaName);
	    FszGroupMeta meta = getOrLoadMeta();
	    lg.debug("Loaded meta:"+meta);
	    if (meta == null) {
	    	throw new FieldParseException("Metadonnees pour '"+metaName+"' non trouvees");
	    }
	    FszGroupMeta monoMeta = meta.getChildMetas().get(0).asGroupMeta();
	    FszGroup mono = (FszGroup) monoMeta.makeNewNode();	    
	    mono.read(is);
	    return mono;
	  }

  /**
   * Retourner le répertoire des métadonnées
   * @return Le répertoire
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
	 * Retourner le nom de la métadonnée
	 * @return le nom
	 */
  public String getMetaName() {
    return metaName;
  }

  /**
   * Attribuer le nom de la métadonnée
   * @param metaName Le nom
   */
  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }

  /**
   * Est-ce qu'on accepte des entrées tronquées
   * @return true si on accepte les entrées tronquées
   */
  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }
  
}
