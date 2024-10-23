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

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

/**
 * Application pour transformer un fichier RHS en fichier .csv
 * Donné à titre d'exemple ; il est maintenant plus facile d'utiliser un script groovy à l'aide de 
 * fr.gpmsi
 * 
 * @author hkaradimas
 *
 */
public class Rhs2Csv
{
  static Logger lg = LogManager.getLogger(Rhs2Csv.class);
  
  Arguments args;
  String inFilePath;
  FszGroupMeta rhsMeta;
  String outDir = ".";
  String enc = "UTF-8";
  String prefix = null;
  SimpleDateFormat edf = new SimpleDateFormat("ddMMyyyy");
  SimpleDateFormat idf = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat fdf = new SimpleDateFormat("dd/MM/yyyy");
  NumberFormat usnf = NumberFormat.getInstance(Locale.US);
  int rhsCount; //number of P counted
  int ruCount; //number of RU counted
  boolean useIdf = false; //use international data format ? Default is to use french data format
  
  static class Arguments {
    String[] args; int p = 0;
    public Arguments(String[] argArray) { args = argArray; }
    public boolean hasMoreArguments() { return p < args.length; }
    public String currentArgument() { return args[p]; }
    public String nextArgument() { return args[p++]; }
  }

  /**
   * Méthode triviale pour tester si une chaîne est vide
   * @param str La chaîne à tester
   * @return true si la chaîne est vide
   */
  public static final boolean isEmpty(String str) {
    return str == null || str.trim().equals("");
  }
  
  /**
   * Constructeur simple
   */
  public Rhs2Csv() {}
  
  void init(String[] argsp) throws Exception
  {
  	usnf.setGroupingUsed(false);
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
          //Logger.getRootLogger().setLevel(Level.DEBUG);
        }
      else if (arg.equals("-useidf")) {
    	  useIdf = true;
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
  
  /**
   * Emettre la ligne d'en-tête 
   * @param gm La définition de groupe
   * @param bw Le BufferedWriter sur lequel émettre
   * @throws IOException _
   */
  public void emitCsvHeader(FszGroupMeta gm, BufferedWriter bw)
      throws IOException 
  {
    String name = gm.getStdName();
    if (name.equals("SLI")) {
      bw.write("SLI_ID;IMPORT_DA;N_RHS");
    }
    else if (name.equals("RHS")) {
      bw.write("RHS_ID;SLI_ID"); //ID + Local Session's Import ID
    }
    else if (name.equals("DA")) {
      bw.write("DA_ID;RHS_ID");
    }
    else if (name.equals("ACS")) {
      bw.write("ACS_ID;RHS_ID");
    }
    else if (name.equals("ACC")) {
      bw.write("ACC_ID;RHS_ID");
    }
    lg.debug("Writing header for "+name);
    List<FszMeta> cms = gm.getChildMetas();
    for (FszMeta cm : cms) {
      if (cm.isFieldMeta()) {
        bw.write(";");
        bw.write(cm.getStdName());
      }
    }//for
    if (name.equals("RUM")) bw.write(";LIGNERHS");
    bw.newLine();
  }
  
  
  void run()
      throws FieldParseException, IOException, ParseException, MissingMetafileException
  {
    RhsReader rrdr = new RhsReader();
    RsCsvHelper rh = new RsCsvHelper();
    rh.outPrefix = prefix;
    rh.outDir = new File(outDir);
    rh.makeStreams("SLI");
    rh.makeStreams("RHS");
    rh.makeStreams("DA");
    rh.makeStreams("ACS");
    rh.makeStreams("ACC");
    
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
          FszNode rhs = rrdr.readOne(line);
          emitCsv((FszGroup)rhs, rh, line);            
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
      bw.write(String.valueOf(rhsCount));
      bw.newLine();
    }
    finally {
      fis.close();
      rh.closeStreams();
    }
  }

  /**
   * Emettre la ligne d'en-tête
   * @param g Le groupe pour lequel on veut émettre l'en-tête
   * @param h Un objet d'assistance pour donner les paramètres à utiliser
   * @param line La ligne qui contient le RHS, elle sera ajoutée en dernier
   * @throws IOException Si erreur E/S
   * @throws ParseException Si problème lors de l'analyse du format
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
          if (groupName.equals("P")) {
            bw.write(";K"+h.getLsiId());
            h.setPId(h.getCurrentId());
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
    if (gm.getGroupName().equals("RHS")) bw.write(";"+line);
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
      Number nr = usnf.parse(value); //parse...
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

  /**
   * Méthode main(), initialise les journaux et appelle {@link Rhs2Csv#init(String[])} , puis {@link Rhs2Csv#run()}
   * @param args Les arguments
   * @throws Exception _
   */
  public static void main(String[] args)
      throws Exception 
  {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
    //Logger.getRootLogger().setLevel(Level.DEBUG);
    Rhs2Csv app = new Rhs2Csv();
    app.init(args);
    app.run();
  }

}
