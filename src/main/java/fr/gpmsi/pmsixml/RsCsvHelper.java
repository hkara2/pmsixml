package fr.gpmsi.pmsixml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Informations pour generer un jeu de fichiers .csv coherent pour des RSS ou
 * des RSA donnes. 
 * @author hk
 *
 */
public class RsCsvHelper
{
  static Logger lg = LogManager.getLogger(RsCsvHelper.class);
  
  File outDir;
  String outPrefix;
  //streams by name : p, da, dad, za, ...
  HashMap<String, Streams> streamsByName = new HashMap<String, RsCsvHelper.Streams>();
  HashMap<String, Boolean> headerEmittedByName = new HashMap<String, Boolean>();
  
  long lastSysTime = System.currentTimeMillis();
  long interCounter;
  long lsiId; //local session import id
  long pId; //id of parent p
  long ruId; //id of parent ru
  
  class Streams {
    OutputStream outStream;
    BufferedWriter wr;
    public void close() throws IOException 
    {
      if (wr != null) {
        wr.flush();
        wr.close();
      }
      if (outStream != null) outStream.close();
    }
  }
  
  public RsCsvHelper() {
    lsiId = getCurrentId();
    advanceIdCounter();
  }

  public void makeStreams(String name)
      throws IOException 
  {
    File f = new File(outDir, outPrefix+name+".csv");
    FileOutputStream outStream = new FileOutputStream(f);
    OutputStreamWriter osw = new OutputStreamWriter(outStream, "UTF-8");
    BufferedWriter wr = new BufferedWriter(osw);
    putStreams(name, outStream, wr);
  }
  
  public void closeStreams() throws IOException {
    for (Streams st : streamsByName.values()) st.close();    
  }
  
  /**
   * The ID counter uses the current unix epoch time in ms, and adds an
   * intermediate counter that goes up to 9999. Every 1000 values 
   * currentTimeMillis is checked for change, and if this is the case the
   * intermediate counter is reset to 0.
   * Here is an example of current time millis for 2016-11-13 (around 11h00) :
   * 1479033124755
   * And here is the Long.MAX_VALUE :
   * 9223372036854775807
   * We can see that there are 6 decimal digits more so this will fit.
   */
  public void advanceIdCounter()
  {
    interCounter++;
    if (interCounter % 1000 == 0) {
      //every 100 increments check if lastSysTime can be updated
      long sysTime = System.currentTimeMillis();
      if (lastSysTime != sysTime) {
        lastSysTime = sysTime; interCounter = 0;
      }
    }
    //if counter overflow, force wait for next systime
    if (interCounter >= 10000) {
      long sysTime = System.currentTimeMillis();
      int attempts = 0;
      while (sysTime == lastSysTime) {
        try { Thread.sleep(1); }
        catch (InterruptedException ignored) { }
        attempts++;
        if (attempts > 1000) {
          //normally impossible
          throw new RuntimeException("Internal error System.currentTimeMillis() doesn't increment");
        }
      }//while
    }
  }
  
  public long getCurrentId()
  {
    return lastSysTime * 10000 + interCounter;
  }

  public void putStreams(String name, OutputStream os, BufferedWriter w) {
    Streams obj = new Streams();
    obj.outStream = os;
    obj.wr = w;
    streamsByName.put(name, obj);
  }
  
  public OutputStream getOutputStream(String name) {
    Streams obj = streamsByName.get(name);
    if (obj == null) return null;
    return obj.outStream;
  }
  
  public BufferedWriter getBufferedWriter(String name) {
    Streams obj = streamsByName.get(name);
    if (obj == null) return null;
    return obj.wr;
  }

  public long getPId() {
    return pId;
  }

  public void setPId(long pId) {
    this.pId = pId;
  }

  public long getRuId() {
    return ruId;
  }

  public void setRuId(long ruId) {
    this.ruId = ruId;
  }
  
  public Boolean isHeaderEmitted(String name) {
    Boolean b = headerEmittedByName.get(name); 
    return b != null && b.booleanValue() == true; 
  }
  
  public void setHeaderEmitted(String name, boolean b) {
    headerEmittedByName.put(name, b);
  }

  public long getLsiId() {
    return lsiId;
  }

  public void setLsiId(long lsiId) {
    this.lsiId = lsiId;
  }
  
}
