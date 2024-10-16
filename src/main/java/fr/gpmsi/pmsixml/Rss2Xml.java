package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.List;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Transformer un fichier RSS en fichier XML.
 * Obsolète ; il vaut mieux passer par l'utilisation de la librairie gpmsi.
 * @author hkaradimas
 *
 */
public class Rss2Xml
{
  static Logger lg = LogManager.getLogger(Rss2Xml.class);
  
  Arguments args;
  String inFilePath;
  String outFilePath;
  FszGroupMeta rss016;
  FszGroupMeta rss017;
  String enc = "UTF-8";
  
  static class Arguments {
    String[] args; int p = 0;
    public Arguments(String[] argArray) { args = argArray; }
    public boolean hasMoreArguments() { return p < args.length; }
    public String currentArgument() { return args[p]; }
    public String nextArgument() { return args[p++]; }
  }

  void init(String[] argsp)
		  throws Exception
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
      else {
        throw new Exception("Argument non reconnu '"+arg+"'");
      }
    }//while
    //vérifie qu'il y a au minimum les deux métadonnées rss016 et rss017 qui se chargent, sinon il y a un problème
    rss016 = new FszGroupMeta("rss016");
    rss016.parse(new InputStreamReader(Rss2Xml.class.getResourceAsStream("/"+FszMeta.PREFIX_DIR+"/rss016.csv"), "UTF-8"));
    rss017 = new FszGroupMeta("rss017");
    rss017.parse(new InputStreamReader(Rss2Xml.class.getResourceAsStream("/"+FszMeta.PREFIX_DIR+"/rss017.csv"), "UTF-8"));
    //StringBuffer sb = new StringBuffer();
    //rss016.dump(sb);
    //System.out.println(sb);
  }
  
  void run()
  		throws FieldParseException, IOException, MissingMetafileException
  {
    RssReader rrdr = new RssReader();
    RsCsvHelper rh = new RsCsvHelper();
    
    if (inFilePath == null) {
      throw new IOException("No input file given !");
    }
    FileInputStream fis = new FileInputStream(inFilePath);
    FileOutputStream fos = new FileOutputStream(outFilePath);
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    BufferedWriter bw = new BufferedWriter(osw);
    try {
    	bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    	bw.newLine();
    	bw.write("<RUMS>"); bw.newLine();
      InputStreamReader isr = new InputStreamReader(fis, enc);
      BufferedReader br = new BufferedReader(isr);
      String line = br.readLine();
      while (line != null) {
        if (line.length() > 0) {
          FszNode g = rrdr.readOne(line);
          //StringBuffer sb = new StringBuffer();
          //g.dump(sb, 0);          bw.write("<RUM>"); 
          //lg.debug(sb);
          bw.write("<RUM>"); 
          emitXml(g, bw);
          bw.write("</RUM>"); bw.newLine(); 
          rh.advanceIdCounter();
        }
        line = br.readLine();
      }
      br.close();
      isr.close();
      bw.write("</RUMS>");
    }
    finally {
      fis.close();
      rh.closeStreams();
      bw.close();
      osw.close();
      fos.close();
    }
    
  }

  public void emitXml(FszNode nd, Writer wr) 
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
  		wr.write(sb.toString());
      List<FszNode> children = nd.getChildren();
      for (FszNode child : children) {
    		emitXml(child, wr);
      }  	  		
      sb = new StringBuffer();
  	}
  	sb.append("</"); sb.append(name); sb.append(">");
  	wr.write(sb.toString());
  }
  
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
   * Escape XML
   * @param args _
   * @throws Exception _
   */
//  private void ex(String str) {
//    StringBuffer sb = new StringBuffer();
//    char[] ca = str.toCharArray();
//    for (int i = 0; i < ca.length; i++) {
//      char c = ca[i];
//      switch (c) {
//      case '<': sb.append("&lt;"); break;
//      case '>': sb.append("&gt;"); break;
//      case '&': sb.append("&amp;"); break;
//      case '\'': sb.append("&apos;"); break;
//      case '\"': sb.append("&quot;"); break;
//      default:
//        sb.append(c);
//      }
//    }
//
//  }
  
  public static void main(String[] args) throws Exception {
    Rss2Xml app = new Rss2Xml();
    app.init(args);
    app.run();
  }

}
