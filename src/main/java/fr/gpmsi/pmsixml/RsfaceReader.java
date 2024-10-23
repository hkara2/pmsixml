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
 * Lecteur de RSF ACE.
 * Lit les métadonnées en fonction de la lettre RSF (a, b, c, h, l, m, p sont supportés pour l'instant).
 * 
 * @author hkaradimas
 *
 */
public class RsfaceReader
{
  static Logger lg = LogManager.getLogger(RsfaceReader.class);
  
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  File metasDir;
  
  String yearOfFormat;
  
  boolean truncatedInputAccepted = true;
  
  /**
   * Constructeur simple
   */
  public RsfaceReader() {
  }

  /**
   * Constructeur avec l'année du format RSF-ACE
   * @param yearOfFormat L'année sur 4 chiffres
   */
  public RsfaceReader(String yearOfFormat) {
  	this.yearOfFormat = yearOfFormat;
  }

  /**
   * Trouver la métadonnée avec le nom (lettre en minuscule)
   * @param letter Exemple "a"
   * @return
   * @throws FieldParseException
   * @throws IOException
   * @throws MissingMetafileException
   */
  private FszGroupMeta loadMeta(String letter)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    String metaName = yearOfFormat+letter.toLowerCase();
    FszGroupMeta meta = new FszGroupMeta(metaName);
    String resourceName = "/fr/gpmsi/pmsixml/rsface"+metaName+".csv";
    MetaFileLoader ldr = new MetaFileLoader(metasDir);
    InputStream ins = ldr.getInputStream(resourceName);
    if (ins == null) throw new MissingMetafileException("Fichier meta non trouve : "+resourceName);
    Reader rdr = new InputStreamReader(ins, "UTF-8");
    try {
      meta.parse(rdr);
      metasByName.put(metaName, meta);
      return meta;
    }
    finally {
      rdr.close();
    }
  }
  
  private FszGroupMeta getOrLoadMeta(String letter)
      throws FieldParseException, IOException, MissingMetafileException
  {
    String metaName = yearOfFormat+letter.toLowerCase();
    FszGroupMeta meta = metasByName.get(metaName);
    if (meta != null) return meta;
    return loadMeta(letter);
  }
    
  /**
   * Lire un RSF-ACE depuis une String
   * @param str La String à partir de laquelle on veut lire
   * @param linenr Le numéro de ligne de cette String dans le fichier
   * @return Un FszGroup qui contient le RSA
   * @throws FieldParseException Si erreur d'analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si un fichier de définition n'a pas été trouvé
   */
  public FszGroup readRSFACE(String str, int linenr)
      throws FieldParseException, IOException, MissingMetafileException
  {
    //get meta for line
    String ver = str.substring(0, 1);
    lg.debug("ver:"+ver);
    FszGroupMeta meta = getOrLoadMeta(ver);
    lg.debug("Loaded meta:"+meta);
    String rsfName = "RSF"+ver.toUpperCase();
    FszGroupMeta rsfaceMeta = meta.findChildGroupMeta(rsfName);
    if (rsfaceMeta == null) {
    	throw new FieldParseException("Metadonnees pour '"+rsfName+"' non trouvees");
    }
    FszGroup rsface = (FszGroup) rsfaceMeta.makeNewNode();
    InputString is = new InputString(str);
    is.acceptTruncated = truncatedInputAccepted;
    is.lineNumber = linenr;
    is.acceptTruncated = true;
    rsface.read(is);
    return rsface;
  }
  
  /**
   * Retourner le repertoire des métadonnées
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
   * Retourner l'année du format RSF-ACE à utiliser
   * @return L'année sur 4 chiffres
   */
  public String getYearOfFormat() {
  	return yearOfFormat;
  }

  /**
   * Définir l'année du format RSF-ACE à utiliser
   * @param yearOfFormat L'année sur 4 chiffres
   */
  public void setYearOfFormat(String yearOfFormat) {
	this.yearOfFormat = yearOfFormat;
  }

  /**
   * Est-ce qu'on accepte les enregistrements tronqués ?
   * @return true (défaut) ou false
   */
  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  /**
   * Est-ce qu'on accepte les enregistrements tronqués ?
   * @param truncatedInputAccepted La valeur boolean à utiliser
   */
  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }

}
