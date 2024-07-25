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
 * Lecteur de RSS
 * @author hk
 *
 */
public class RssReader
{
  static Logger lg = LogManager.getLogger(RssReader.class);
  
  String[] supportedVersions = {"016", "017"}; //obsolete, sera enleve prochainement
  
  HashMap<String, FszGroupMeta> metasByName = new HashMap<String, FszGroupMeta>();
  
  File metasDir;

  boolean truncatedInputAccepted = true;
  
  int lineNr = -1;
  
  public RssReader()
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
    String resourceName = "/rss"+name+".csv";
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

  public FszNode readOne(String rss)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    return readOne(rss, -1);
  }
  
  /**
   * Lire une ligne de RSS (un RUM).
   * @param rss La ligne de RUM (du RSS) à lire
   * @param lineNr Le numero de ligne
   * @return Un noeud de type conteneur qui contient le RSS.
   * @throws FieldParseException Si erreur d'analyse de champ
   * @throws IOException Si erreur d'entrée-sortie
   * @throws MissingMetafileException Si le fichier de métadonnées n'a pas été retrouvé
   */
  public FszNode readOne(String rss, int lineNr)
      throws FieldParseException, IOException, MissingMetafileException 
  {
    String ver = rss.substring(9, 12); //hack (spécifique au format RSS) pour avoir la version
    this.lineNr = lineNr;
    try {
      lg.debug("ver:"+ver);
      FszGroupMeta meta = getOrLoadMeta(ver);
      lg.debug("Loaded meta:"+meta);
      FszGroupMeta rumMeta = meta.findChildGroupMeta("RUM");
      if (rumMeta == null) {
          throw new FieldParseException("Pas de meta information retrouvee pour 'RUM'");
      }
      FszNode nd = rumMeta.makeNewNode();
      //HashMap<String, Integer> countersByGroupName = new HashMap<String, Integer>();
      InputString in = new InputString(rss);
      in.lineNumber = lineNr;
      in.acceptTruncated = truncatedInputAccepted;
      nd.read(in);
      //nd.readTwoLevels(in, countersByGroupName);
      return nd;      
    }
    catch (FieldParseException fpex) {
      fpex.setLineNr(lineNr); //ajouter l'information du numero de ligne
      throw fpex;
    }
  }
  
  public static void main(String[] args)
      throws Exception
  {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
    //Logger.getRootLogger().setLevel(Level.DEBUG);
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
