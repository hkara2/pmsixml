package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;

/**
 * Transformer un fichier RSS en fichier .csv avec les principaux champs présents.
 * @author hkaradimas
 *
 */
public class Rss2Csv
{
  static Logger lg = LogManager.getLogger(Rss2Csv.class);
  
  Arguments args;
  String inFilePath;
  String outDir = ".";
  String enc = "UTF-8";
  String prefix = null;
  SimpleDateFormat edf = new SimpleDateFormat("ddMMyyyy");
  SimpleDateFormat idf = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat fdf = new SimpleDateFormat("dd/MM/yyyy");
  NumberFormat usnf = NumberFormat.getInstance(Locale.US);
  int rumCount; //number of RUMs counted
  boolean useIdf = false; //use international data format ? Default is to use french data format
  boolean canRun = true; //can we run ?
  
  static class Arguments {
    String[] args; int p = 0;
    public Arguments(String[] argArray) { args = argArray; }
    public boolean hasMoreArguments() { return p < args.length; }
    public String currentArgument() { return args[p]; }
    public String nextArgument() { return args[p++]; }
  }

  /**
   * Méthode triviale pour tester si une chaîne est vide.
   * @param str La chaîne à tester
   * @return true si la chaîne est vide
   */
  public static final boolean isEmpty(String str) {
    return str == null || str.trim().equals("");
  }
  
  void init(String[] argsp)
  		throws Exception
  {
  	usnf.setGroupingUsed(false); //ne pas utiliser de symboles de groupage
    args = new Arguments(argsp);
    while (args.hasMoreArguments()) {
      String arg = args.nextArgument();
      if (arg.equals("-in")) {
        inFilePath = mandatory(args, "Argument manquant pour "+arg);
      }
      else if (arg.equals("-outdir")) {
        outDir = mandatory(args, "Argument manquant pour "+arg);
      }
      else if (arg.equals("-enc")) {
        enc = mandatory(args, "Argument manquant pour "+arg);
      }
      else if (arg.equals("-debug")) {
        Log4j2Utils.changeRootLogLevel(Level.DEBUG);
      }
      else if (arg.equals("-useidf")) {
    	  useIdf = true;
      }
      else if (arg.equals("-help")) {
    	  printHelp(); canRun = false;
    	  return;
      }
      else if (arg.equals("-prefix")) {
        prefix = mandatory(args, "Argument manquant pour "+arg);
      }
      else {
        throw new Exception("Argument non reconnu '"+arg+"'");
      }
    }//while
    if (prefix == null) {
      SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmm");
      prefix = df.format(new Date());
    }
  }
  
  void printHelp() {
  	StringBuffer sb = new StringBuffer();
  	String nl = System.getProperty("line.separator");
  	sb.append("Utilisation : Rss2Csv <arguments> "); sb.append(nl);
  	sb.append("Arguments :"); sb.append(nl);
  	sb.append("  -in <fichier entree> : le fichier a utiliser en entree. Obligatoire."); sb.append(nl);
  	sb.append("  -outdir <chemin> : designe le repertoire de sortie."); sb.append(nl);
  	sb.append("  -enc <encodage> : donne l'encodage a utiliser pour les fichiers de sortie."); sb.append(nl);
  	sb.append("  -debug : active les messages de debogage."); sb.append(nl);
  	sb.append("  -prefix <prefixe> : designe le prefixe a utiliser pour les noms de fichier"); sb.append(nl);
  	sb.append("  -useidf : utiliser le format de date iso (aaaa-mm-jj). Par defaut le format francais (jj/mm/aaaa) est utilise."); sb.append(nl);
  	sb.append("  -help : afficher l'aide puis sortir."); sb.append(nl);
  	System.out.println(sb.toString());
  }
  
  public void emitCsvHeader(FszGroupMeta gm, BufferedWriter bw)
      throws IOException 
  {
    String name = gm.getStdName();
    if (name.equals("SLI")) {
      bw.write("SLI_ID;IMPORT_DA;N_RUM");
    }
    else if (name.equals("RUM")) {
      bw.write("RUM_ID;SLI_ID"); //ID + Local Session's Import ID
    }
    else if (name.equals("DA")) {
      bw.write("DA_ID;RUM_ID");
    }
    else if (name.equals("DAD")) {
      bw.write("DAD_ID;RUM_ID");
    }
    else if (name.equals("ZA")) {
      bw.write("ZA_ID;RUM_ID");
    }
    lg.debug("Writing header for "+name);
    List<FszMeta> cms = gm.getChildMetas();
    for (FszMeta cm : cms) {
      if (cm.isFieldMeta()) {
        bw.write(";");
        bw.write(cm.getStdName());
      }
    }//for
    if (name.equals("RUM")) bw.write(";LIGNERSS");
    bw.newLine();
  }
  
  
  void run()
      throws FieldParseException, IOException, ParseException, MissingMetafileException
  {
  	if (!canRun) return;
    RssReader rrdr = new RssReader();
    RsCsvHelper rh = new RsCsvHelper();
    rh.outPrefix = prefix;
    rh.outDir = new File(outDir);
    rh.makeStreams("SLI");
    rh.makeStreams("RUM");
    rh.makeStreams("DA");
    rh.makeStreams("DAD");
    rh.makeStreams("ZA");
    
    if (inFilePath == null) {
      throw new IOException("No input file given !");
    }
    FileInputStream fis = new FileInputStream(inFilePath);
    try {
      InputStreamReader isr = new InputStreamReader(fis, enc);
      BufferedReader br = new BufferedReader(isr);
      String line = br.readLine();
      while (line != null) {
        if (line.length() > 0) {
          FszNode rum = rrdr.readOne(line);
          //StringBuffer sb = new StringBuffer();
          //g.dump(sb, 0);
          //lg.debug(sb);
          emitCsv((FszGroup)rum, rh, line);            
          rh.advanceIdCounter();
        }
        line = br.readLine();
      }
      br.close();
      isr.close();
      //now emit final SLI file
      BufferedWriter bw = rh.getBufferedWriter("SLI");
      FszGroupMeta sliGm = new FszGroupMeta("SLI");
      emitCsvHeader(sliGm, bw);
      bw.write("K"+String.valueOf(rh.getLsiId()));
      bw.write(';');
      bw.write(edf.format(new Date()));
      bw.write(';');
      bw.write(String.valueOf(rumCount));
      bw.newLine();
    }
    finally {
      fis.close();
      rh.closeStreams();
    }
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
    if (!h.isHeaderEmitted(groupName)) {
      emitCsvHeader(gm, bw);
      h.setHeaderEmitted(groupName, true);
    }
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
            rumCount++;
          }
          else {
            bw.write(";K"+h.getPId());
          }
        }
        String v = convertValue(childField);
        lg.debug(i+"::"+fm.getStdName()+", value : "+v);
        bw.write(";"+v);
        i++;
      }
      else {
        //it's group meta, call emitCsv recursively
        emitCsv((FszGroup)child, h, line);
      }
    }
    if (gm.getGroupName().equals("RUM")) bw.write(";"+line);
    if (!g.isContainer()) {
      bw.newLine();
      h.advanceIdCounter();
    }
  }
  
  String convertValue(FszField fld)
      throws ParseException 
  {
    if (fld.representsEuropeanDate()) {
      String value = fld.getValue();
      DateFormat df = fdf;
      if (useIdf) df = idf;
      if (isEmpty(value)) return value;
      else return df.format(edf.parse(value));
    }
    if (fld.representsNumber()) {
      String value = fld.getValue();
      if (isEmpty(value)) return value;
      lg.debug("Parsing '"+value+"'...");
      Number nr = usnf.parse(value.trim()); //parse...
      return usnf.format(nr); //...and reformat
    }
    //return as it is
    return fld.getValue();
  }
  
  String mandatory(Arguments args, String message)
  throws Exception
  {
    if (!args.hasMoreArguments()) {
      throw new Exception(message);
    }
    return args.nextArgument();
  }

  public static void main(String[] args)
      throws Exception 
  {
    Rss2Csv app = new Rss2Csv();
    app.init(args);
    app.run();
  }

}
