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
  
  /**
   * Objet pour garder les flux ensemble, pour pouvoir les refermer ensemble.
   */
  class Streams {
	/** Le flux qui sert à écrire dans le fichier */ 
    OutputStream outStream;
    /** Le flux dans lequel l'écriture se fait dans les méthodes. Normalement écrit dans outStream */
    BufferedWriter wr;
    
    /**
     * Ferme d'abord wr, puis outStream.
     * @throws IOException Si erreur E/S
     */
    public void close() throws IOException 
    {
      if (wr != null) {
        wr.flush();
        wr.close();
      }
      if (outStream != null) outStream.close();
    }
  }
  
  /**
   * Constructeur simple.
   * Prend un nouvel Id.
   */
  public RsCsvHelper() {
    lsiId = getCurrentId();
    advanceIdCounter();
  }

  /**
   * Initialise des flux (Streams) pour pouvoir envoyer les données
   * @param name Le nom (sera utilisé dans le fichier .csv de sortie)
   * @throws IOException Si erreur d'E/S
   */
  public void makeStreams(String name)
      throws IOException 
  {
    File f = new File(outDir, outPrefix+name+".csv");
    FileOutputStream outStream = new FileOutputStream(f);
    OutputStreamWriter osw = new OutputStreamWriter(outStream, "UTF-8");
    BufferedWriter wr = new BufferedWriter(osw);
    putStreams(name, outStream, wr);
  }
  
  /**
   * Fermer tous les flux, pour tous les noms
   * @throws IOException Si erreur E/S
   */
  public void closeStreams() throws IOException {
    for (Streams st : streamsByName.values()) st.close();    
  }
  
  /**
   * Le compteur d'ID utilise le temps courant ('unix epoch') en ms, et
   * ajoute un compteur intermédiaire qui va jusqu'à 9999.<br>
   * Toutes les 1000 valeurs, currentTimeMillis est vérifié pour voir
   * s'il a changé, et si c'est le cas le compteur intermédiaire est
   * remis à 0.<br>
   * Voici un exemple de current time millis pour 2016-11-13 (vers 11h00) :<br>
   * <code>1479033124755</code><br>
   * Et voici la valeur de <code>Long.MAX_VALUE</code> :<br>
   * <code>9223372036854775807</code><br>
   * Nous pouvons constater qu'avec 6 décimales de plus, la valeur 
   * entre largement.
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
  
  /**
   * Retourner l'ID courant. Retournera toujours la même valeur tant que {@link #advanceIdCounter()} n'aura
   * pas été appelé.
   * @return l'ID courant
   */
  public long getCurrentId()
  {
    return lastSysTime * 10000 + interCounter;
  }

  /**
   * Enregistre les flux (Streams)
   * @param name Nom des flux (est utilisé notamment dans le fichier de sortie)
   * @param os Le flux de sortie
   * @param w Le writer dans lequel envoyer les données
   */
  public void putStreams(String name, OutputStream os, BufferedWriter w) {
    Streams obj = new Streams();
    obj.outStream = os;
    obj.wr = w;
    streamsByName.put(name, obj);
  }
  
  /**
   * Retourner le {@link OutputStream} pour le nom donné
   * @param name Le nom de l'ensembe de streams
   * @return Le OutputStream
   */
  public OutputStream getOutputStream(String name) {
    Streams obj = streamsByName.get(name);
    if (obj == null) return null;
    return obj.outStream;
  }
  
  /**
   * Rertourner le {@link BufferedWriter} pour le nom donné
   * @param name Le nom de l'ensemble de flux
   * @return Le BufferedWriter
   */
  public BufferedWriter getBufferedWriter(String name) {
    Streams obj = streamsByName.get(name);
    if (obj == null) return null;
    return obj.wr;
  }

  /**
   * Retourner le pId (utilisé lorsque du .csv est émis)
   * @return le pId
   */
  public long getPId() {
    return pId;
  }

  /**
   * Définir le pId (principal ID) qui est l'ID du RSA ou du RSS.
   * @param pId Le pId
   */
  public void setPId(long pId) {
    this.pId = pId;
  }

  /**
   * Retourner le ruId (utilisé lorsque du .csv est émis)
   * @return le ruId
   */
  public long getRuId() {
    return ruId;
  }

  /**
   * Définir le ruId qui est l'ID du sous-groupe RU (un des RUMs d'un RSA)
   * @param ruId Le ruId
   */
  public void setRuId(long ruId) {
    this.ruId = ruId;
  }
  
  /**
   * Est-ce que l'en-tête a déjà été émise pour le flux nommé ?
   * @param name Le nom du flux
   * @return true si c'est le cas
   */
  public Boolean isHeaderEmitted(String name) {
    Boolean b = headerEmittedByName.get(name); 
    return b != null && b.booleanValue() == true; 
  }
  
  /**
   * Définir si l'en-tête a déjà été émise pour le flux donné.
   * @param name Nom du flux
   * @param b true si le flux a déjà été émis
   */
  public void setHeaderEmitted(String name, boolean b) {
    headerEmittedByName.put(name, b);
  }

  /**
   * Retourner le local session ID
   * @return le lsiId
   */
  public long getLsiId() {
    return lsiId;
  }

  /**
   * Définir le lsiId
   * @param lsiId le lsiId
   */
  public void setLsiId(long lsiId) {
    this.lsiId = lsiId;
  }
  
}
