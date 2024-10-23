package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.List;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Application simple permettant de convertir des champs fixes (Fixed SiZe) en XML,
 * en utilisant les noms fournis par les métadonnées.
 * Arguments en ligne de commande : 
 * <ul>
 * <li>-in &lt;fichier-d-entree&gt;
 * <li>-out &lt;fichier-de-sortie&gt;
 * <li>-m &lt;nom-metadonnee&gt;
 * </ul>
 * <p>
 * Exemple :
 * <pre>
 * java fr.gpmsi.pmsixml.Fsz2Xml -in FICHCOMPMED -out FICHCOMPMED.xml -m fichcompmed2018
 * </pre>
 * 
 * @author hkaradimas
 *
 */
public class Fsz2Xml
{
  static Logger lg = LogManager.getLogger(Fsz2Xml.class);
  
  Arguments args;
  String inFilePath;
  String outFilePath;
  String fszMetaName = null;
  MetaFileLoader mfl;
  FszGroupMeta meta;
  String enc = "UTF-8";
  
  static class Arguments {
    String[] args; int p = 0;
    public Arguments(String[] argArray) { args = argArray; }
    public boolean hasMoreArguments() { return p < args.length; }
    public String currentArgument() { return args[p]; }
    public String nextArgument() { return args[p++]; }
  }

  /** Constructeur simple */
  public Fsz2Xml() {}
  
  void init(String[] argsp) throws Exception
  {
    args = new Arguments(argsp);
    while (args.hasMoreArguments()) {
      String arg = args.nextArgument();
      if (arg.equals("-in")) {
        inFilePath = mandatory(args, "Argument manquant pour "+arg);
      }
      else if (arg.equals("-out")) {
        outFilePath = mandatory(args, "Argument manquant pour "+arg);
      }
      else if (arg.equals("-m")) {
        fszMetaName = mandatory(args, "Argument manquant pour "+arg);
      }
      else {
        throw new Exception("Argument non reconnu '"+arg+"'");
      }
    }//while
    File metaFile = new File(fszMetaName);
    if (metaFile.exists()) {
      //C'est un nom de fichier de métadonnées, comme par ex. "C:\t\resources\fichcompmed2018.csv"
      //Le répertoire sera le parent du fichier, par ex. "C:\t\resources"
      //Le nom sera le nom du fichier moins le suffixe, ex. "fichcompmed2018"
      mfl = new MetaFileLoader(metaFile.getAbsoluteFile().getParentFile());
      meta = mfl.loadMeta("/"+metaFile.getName());
    }
    else {
      mfl = new MetaFileLoader();
      if (!fszMetaName.toLowerCase().endsWith(".csv")) {
        fszMetaName = fszMetaName + ".csv";
      }
      if (!fszMetaName.startsWith("/")) fszMetaName = "/"+FszMeta.PREFIX_DIR+"/"+fszMetaName;
      meta = mfl.loadMeta(fszMetaName);
    }
  }
  
  void run()
  		throws FieldParseException, IOException, MissingMetafileException
  {
    //FszReader frdr;
    RsCsvHelper rh = new RsCsvHelper();
    
    if (inFilePath == null) {
      throw new IOException("Pas de fichier d'entree donne !");
    }
    FileInputStream fis = new FileInputStream(inFilePath);
    FileOutputStream fos = new FileOutputStream(outFilePath);
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    BufferedWriter bw = new BufferedWriter(osw);
    String metaName = fszMetaName;
    while (metaName.startsWith("/")) metaName = metaName.substring(1);
    if (metaName.toLowerCase().endsWith(".csv")) metaName = metaName.substring(0, metaName.length()-4);
    try {
    	bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    	bw.newLine();
    	bw.write("<"+metaName+">"); bw.newLine();
      InputStreamReader isr = new InputStreamReader(fis, enc);
      BufferedReader br = new BufferedReader(isr);
      FszReader fRdr = new FszReader(mfl, fszMetaName, meta.getReadStrategy().getName());
      InputString instr = new InputString(br.readLine());
      while (instr.line != null) {
        if (instr.line.length() > 0) {
          FszNode g = fRdr.readOne(instr);
          emitXml(g, bw);
          bw.newLine(); 
          rh.advanceIdCounter();
        }
        instr.nextLine(br.readLine()); 
      }
      br.close();
      isr.close();
      bw.write("</"+metaName+">");
    }
    finally {
      fis.close();
      rh.closeStreams();
      bw.close();
      osw.close();
      fos.close();
    }
    
  }

  void emitXml(FszNode nd, BufferedWriter bw) 
  		throws IOException 
  {
  	StringBuffer sb = new StringBuffer();
  	String name = nd.getMeta().getStdName();
  	if (nd.isContainer()) name = "G_" + name;
  	sb.append("<"); sb.append(name); sb.append(">");
  	if (nd.isField()) {
  		FszField fld = (FszField) nd;
  		sb.append(fld.getValue());
  	}
  	else if (nd.isFieldGroup()) {
  		bw.write(sb.toString());
      List<FszNode> children = nd.getChildren();
      for (FszNode child : children) {
    		emitXml(child, bw);
      }  	  		
      sb = new StringBuffer();
  	}
  	sb.append("</"); sb.append(name); sb.append(">");
  	bw.write(sb.toString());
  }
  
  /**
   * Emettre le contenu en tant que .csv
   * @param g Le groupe pour lequel on veut émettre du csv
   * @param h Un objet qui aide à donner les bonnes options que l'on veut pour le csv
   * @param line Une ligne qui est émise à la fin pour le groupe "P"
   * @throws IOException _
   * @throws ParseException _
   */
  public void emitCsv(FszGroup g, RsCsvHelper h, String line)
      throws IOException, ParseException 
  {
    //lg.debug("emitting csv for '"+g.meta.groupName+"'");
    FszGroupMeta gm = (FszGroupMeta) g.getMeta();
    String groupName = gm.getGroupName();
    if (groupName.endsWith("*")) groupName = groupName.substring(0, groupName.length()-1);
    lg.debug("group name : '"+groupName+"'");
    BufferedWriter bw = h.getBufferedWriter(groupName);
    if (bw == null) throw new IOException("Could not find BufferedWriter for '"+groupName+"'");
    int i = 0;
    List<FszNode> children = g.getChildren();
    for (FszNode child : children) {
      lg.debug("child "+child);
      FszMeta childMeta = child.getMeta();
      if (childMeta.isFieldMeta()) {
        FszField childField = (FszField) child;
        FszFieldMeta fm = (FszFieldMeta) childMeta;
        if (i == 0) {
          bw.write("K"+h.getCurrentId());
          if (groupName.equals("RUM")) {
            bw.write(";K"+h.getLsiId());
            h.setPId(h.getCurrentId());
          }
          else {
            bw.write(";K"+h.getPId());
          }
        }
        String v = childField.getValue();
        lg.debug(i+"::"+fm.getStdName()+", value : "+v);
        bw.write(";"+v);
        i++;
      }
      else {
        //it's group meta, call emitCsv recursively
        emitCsv((FszGroup)child, h, line);
      }
    }
    if (gm.getGroupName().equals("P")) bw.write(";"+line);
    if (!g.isContainer()) {
      bw.newLine();
      h.advanceIdCounter();
    }
  }
  
  
  String mandatory(Arguments args, String message)
  throws Exception
  {
    if (!args.hasMoreArguments()) {
      throw new Exception(message);
    }
    return args.nextArgument();
  }
  
  /**
   * Main
   * @param args Arguments
   * @throws Exception Exception lançée
   */
  public static void main(String[] args) throws Exception {
    Fsz2Xml app = new Fsz2Xml();
    app.init(args);
    app.run();
  }

}
