package fr.gpmsi.pmsixml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Recherche et charge un fichier de métadonnées Fsz par nom et un répertoire donné.
 * Exemple d'utilisation :
 * <pre>
 *       MetaFileLoader mfl = new MetaFileLoader(new File("C:\\metadonnees_locales"));
 *       FszGroupMeta gmeta = mfl.getOrLoadMeta("/fr/gpmsi/pmsixml/fichcompmed2020.csv");
 *       fichcompmed = gmeta.getFirstChildGroupMeta();
 *       g = fichcompmed.makeBlankInstance();
 *       g.NADL.value = '900123456';
 *       //etc.
 * </pre>
 * @author hkaradimas
 *
 */
public class MetaFileLoader {
    static Logger lg = LogManager.getLogger(MetaFileLoader.class);
    
	File metaFilesDir;
	HashMap<String, FszGroupMeta> metasByName = new HashMap<>();
	
	/**
	 * constructeur par défaut
	 */
	public MetaFileLoader() {
	}

	/**
	 * Constructeur avec le répertoire des métadonnées
	 * @param dir Le répertoire des métadonnées
	 */
	public MetaFileLoader(File dir) {
		this.metaFilesDir = dir;
	}

	/**
	 * retourner le répertoire des métadonnées
	 * @return Le répertoire des métadonnées
	 */
	public File getMetaFilesDir() {
		return metaFilesDir;
	}

	/**
	 * Définir le répertoire des métadonnées
	 * @param metaFilesDir Le répertoire des métadonnées
	 */
	public void setMetaFilesDir(File metaFilesDir) {
		this.metaFilesDir = metaFilesDir;
	}

	/**
	 * Rechercher un InputStream spécial pour pmsixml, soit par le metaFilesDir (qui est toujours recherché en premier)
	 * soit par les resources, qui sont dans le jar de distribution.
	 * Pour les resources, si le nom de resource ne commence pas par un "/", alors le préfixe "/fr/gpmsi/pmsixml/"
	 * est ajouté devant le nom de ressource . Sinon le nom est laissé tel quel (déconseillé, un mot de warning est mis dans
	 * le log).
	 * @param resourceName le nom de la ressource à rechercher. Devrait contenir le préfixe /fr/gpmsi/pmsixml/ .
	 * @return Le {@link InputStream} correspondant
	 * @throws FileNotFoundException Si la ressource n'a pas été retrouvée dans le système de fichiers
	 * @throws MissingMetafileException Si le fichier de métadonnées n'existe pas dans les ressources de fichier
	 */
	public InputStream getInputStream(String resourceName)
			throws FileNotFoundException, MissingMetafileException
	{
	    InputStream ins = null;
	    if (metaFilesDir != null) {
	        File fileForMeta = new File(metaFilesDir, resourceName);
	        if (fileForMeta.exists() && !fileForMeta.isDirectory()) {
	          ins = new FileInputStream(fileForMeta);
	        }
	        if (ins == null) {
	        	lg.debug("non trouve : '"+fileForMeta+"', recherche dans les ressources");
	        }
	        else return ins;
	    }
	    String resourcePath = resourceName;
	    if (!resourcePath.startsWith("/")) {
	      lg.warn("Ajout du prefixe /"+FszMeta.PREFIX_DIR+"/ devant '"+resourcePath+"', corriger l'appel pour que ce prefixe soit ajoute d'emblee");
	      resourcePath = "/"+FszMeta.PREFIX_DIR+"/"+resourcePath;
	    }
	    ins = MetaFileLoader.class.getResourceAsStream(resourcePath);
	    if (ins == null) {
	      String msg1 = "";
	      if (metaFilesDir != null) msg1 = ", ni dans le repertoire '"+metaFilesDir+"', ni dans ";	      
	      else msg1 = " dans ";
	      throw new MissingMetafileException("Ne trouve pas la resource '"+resourceName+"'"+msg1+"/"+FszMeta.PREFIX_DIR);
	    }
	    return ins;
	}
    
  /**
   * Charger un FszGroupMeta par son nom (doit avoir le préfixe /fr/gpmsi/pmsixml/)
   * @param name le nom de la métadonnée de groupe
   * @return La métadonnée du groupe
   * @throws FieldParseException Si erreur d'analyse de la définition
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si définition de métadonnées non trouvées 
   */
  public FszGroupMeta loadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    FszGroupMeta meta = new FszGroupMeta(name);
    String resourceName = name;
    InputStream ins = getInputStream(resourceName);
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
   * Rechercher un fichier meta soit en ressource, soit dans le répertoire des fichiers meta.
   * Ex : getOrLoadMeta("/fr/gpmsi/pmsixml/tra2016.csv").
   * Le FszGroupMeta est mis en cache, et donc si il est demandé à nouveau par cette méthode,
   * il est pris directement dans le cache.
   * @param name Le nom du fichier meta (doit avoir le préfixe /fr/gpmsi/pmsixml/)
   * @return Le {@link FszGroupMeta} trouvé
   * @throws FieldParseException si erreur d'analyse du fichier meta retrouvé
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si le fichier meta n'existe pas
   */
  public FszGroupMeta getOrLoadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException
  {
    FszGroupMeta meta = metasByName.get(name);
    if (meta != null) return meta;
    return loadMeta(name);
  }
  
}
