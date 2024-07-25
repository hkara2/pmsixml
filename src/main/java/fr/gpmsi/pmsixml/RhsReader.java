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
  
  public FszNode readOne(String rhs, boolean acceptTruncated) 
      throws FieldParseException, IOException, MissingMetafileException
  {
    return readOne(rhs, -1, acceptTruncated);
  }
  
  public FszNode readOne(String rhs) 
  		throws FieldParseException, IOException, MissingMetafileException
  {
  	return readOne(rhs, -1, false);
  }
  
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

  public int getLineNr() {
    return lineNr;
  }

  public void setLineNr(int lineNr) {
    this.lineNr = lineNr;
  }

}
