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
 * Lecteur de fichiers RSA.
 * @author hkaradimas
 *
 */
public class RsaReader
{
  static Logger lg = LogManager.getLogger(RsaReader.class);
  
  String[] supportedVersions = {"222", "223", "224"}; //purement informatif
  
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  File metasDir;
  
  boolean truncatedInputAccepted = true;
  
  public RsaReader() {
  }

  private FszGroupMeta loadMeta(String name)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    FszGroupMeta meta = new FszGroupMeta(name);
    String resourceName = "/rsa"+name+".csv";
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
  
  public static void main(String[] args) {
  }

  public File getMetasDir() {
    return metasDir;
  }

  public void setMetasDir(File metasDir) {
    this.metasDir = metasDir;
  }

  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }

}
