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
 * Lecteur de fichiers RSA (Résumé de Sortie Anonymisé).
 * Après avoir créé l'objet, il faut le configurer en définissant le répertoire qui contient les
 * métadonnées, à l'aide de la méthode {@link #setMetasDir(File)}.
 * Par défaut, l'objet lecteur accepte les lignes tronquées, c'est à dire qui font moins que la longueur standard.
 * Si on veut que l'objet lecteur n'accepte pas les lignes tronquées, appeler <code>setTruncatedInputAccepted(false)</code>
 * @author hkaradimas
 *
 */
public class RsaReader
{
  static Logger lg = LogManager.getLogger(RsaReader.class);
  
  /**
   * Liste à titre indicatif des versions supportées.
   */
  public String[] supportedVersions = {
		  //purement informatif, mais essayer de garder à jour
		  "218", "219", "220", "221", "222", "223", "224"
  };
  
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  File metasDir;
  
  boolean truncatedInputAccepted = true;
  
  /**
   * Constructeur simple
   */
  public RsaReader() {
  }

  private FszGroupMeta loadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    FszGroupMeta meta = new FszGroupMeta(name);
    //240913 hk les resources sont maintenant dans fr.gpmsi.pmsixml et plus à la racine
    //normalement le MetaFileLoader s'en occupe
    String resourceName = "/fr/gpmsi/pmsixml/rsa"+name+".csv";
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
  
  private FszGroupMeta getOrLoadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException
  {
    FszGroupMeta meta = metasByName.get(name);
    if (meta != null) return meta;
    return loadMeta(name);
  }
    
  /**
   * Lire une ligne au format RSA de l'ATIH
   * @param str La ligne qui contient le RSA à lire
   * @param linenr Le numéro de la ligne dans le fichier d'entrée (sert comme information en cas d'erreur)
   * @return Le groupe qui contient les informations du RSA
   * @throws FieldParseException Si erreur de lecture de champ
   * @throws IOException Si erreur d'E/S
   * @throws MissingMetafileException Si un fichier de métadonnées n'a pas pu être trouvé pour ce fichier de RSA
   */
  public FszGroup readRSA(String str, int linenr)
      throws FieldParseException, IOException, MissingMetafileException
  {
    //get meta
    String ver = str.substring(9, 12);
    lg.debug("ver:"+ver);
    FszGroupMeta meta = getOrLoadMeta(ver);
    lg.debug("Loaded meta:"+meta);
    FszGroupMeta rsaMeta = meta.findChildGroupMeta("RSA");
    if (rsaMeta == null) {
    	throw new FieldParseException("Metadonnees pour 'RSA' non trouvees");
    }
    FszGroup rsa = (FszGroup) rsaMeta.makeNewNode();
    InputString is = new InputString(str);
    is.acceptTruncated = truncatedInputAccepted;
    is.lineNumber = linenr;
    rsa.read(is);
//    rsa.readLeafs(is);
//    FszGroup ags = rsa.readSubGroups(is, "AG");
//    if (ags != null) rsa.addChild(ags);
//    FszGroup srs = rsa.readSubGroups(is, "SR");
//    if (srs != null) rsa.addChild(srs);
//    FszGroup rus = rsa.readSubGroups(is, "RU");
//    if (rus != null) {
//      rsa.addChild(rus);
//      List<FszNode> childRus = rus.getChildren();
//      lg.debug("Nombre de RU : "+childRus.size());
//      lg.debug("Lecture des DA");
//      for (FszNode childRuNd : childRus) {
//        FszGroup childRu = (FszGroup) childRuNd;
//        FszGroup das = ((FszGroup)childRu).readSubGroups(is, "DA");
//        childRu.addChild(das);        
//      }
//      lg.debug("Lecture des ZA");
//      for (FszNode childRuNd : childRus) {
//        FszGroup childRu = (FszGroup) childRuNd;
//        FszGroup zas = ((FszGroup)childRu).readSubGroups(is, "ZA");
//        childRu.addChild(zas);
//      }
//    }
//    else {
//      lg.error("Pas trouve de RU !");
//    }
    return rsa;
  }

  /**
   * Retourner le répertoire où chercher des métadonnées supplémentaires
   * @return Le répertoire ou null si aucun répertoire de métadonnées supplémentaires n'a été défini
   */
  public File getMetasDir() {
    return metasDir;
  }

  /**
   * Définir un répertoire où des métadonnées supplémentaires vont être recherchées.
   * Ce répertoire est recherché avant de rechercher les fichiers resource ; on peut
   * donc remplacer tous les fichiers de métadonnées que l'on veut.
   * @param metasDir Le répertoire (peut être null)
   */
  public void setMetasDir(File metasDir) {
    this.metasDir = metasDir;
  }

  /**
   * Est-ce que l'on accepte des lignes tronquées ?
   * Si oui, lorsqu'il n'y aura plus de caractères à lire, un espace sera lu à la place
   * @return true si c'est le cas
   */
  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  /**
   * Définir si des lignes tronquées sont acceptées en entrée.
   * Par défaut : true.
   * @param truncatedInputAccepted true si c'est le cas
   */
  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }

}
