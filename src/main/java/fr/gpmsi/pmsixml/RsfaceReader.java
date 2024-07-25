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
  
  public RsfaceReader() {
  }

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
    String resourceName = "/rsface"+metaName+".csv";
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
  
  public static void main(String[] args) {
  }

  public File getMetasDir() {
    return metasDir;
  }

  public void setMetasDir(File metasDir) {
    this.metasDir = metasDir;
  }

	public String getYearOfFormat() {
		return yearOfFormat;
	}

	public void setYearOfFormat(String yearOfFormat) {
		this.yearOfFormat = yearOfFormat;
	}

  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }

}
