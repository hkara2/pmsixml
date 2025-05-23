package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Exemple de classe qui émet un RSA sous forme de fichier .csv pour les principaux champs.<br>
 * Pour le groupe principal il y a un fichier RSA.csv <br>
 * Le fichier RSA.csv contient comme dernier champ la ligne du RSA au format ATIH telle qu'elle a été lue<br>
 * Pour chaque sous-groupe il y a un fichier csv :
 * <ul>
 * <li><code>RU</code> : RUM
 * <li><code>SR</code> : Suppléments de radiothérapie
 * <li><code>DA</code> : Diagnostics associés du RUM
 * <li><code>ZA</code> : Zone d'actes du RUM
 * </ul>
 * Cela donne des fichiers qui peuvent être utilisés en tant que table.<br>
 * Un système d'identifiants est utilisé qui permet de faire le chaînage père-enfant.<br>
 * Ces numéros sont uniques dans le temps, ce qui permet de les utiliser comme clé primaire si
 * on veut importer les données dans une table de base de données.
 * <br>
 * Un fichier SLI.csv (pour Session Locale d'Import) est émis à la fin, et contient les données d'import :
 * <ul>
 * <li>Numéro unique
 * <li>Date d'import
 * <li>Nombre de RSA traités
 * <li>Nombre de RUMs traités
 * </ul>
 * <p>
 * Laissé à titre d'illustration.
 * Il est maintenant plus facile d'utiliser la librairie gpmsi.
 * </p>
 * @author hkaradimas
 *
 */
public class Rsa2Csv
{
  static Logger lg = LogManager.getLogger(Rsa2Csv.class);
  
  Arguments args;
  String inFilePath;
  //FszGroupMeta rss016;
  //FszGroupMeta rss017;
  String outDir = ".";
  String enc = "UTF-8";
  String prefix = null;
  SimpleDateFormat edf = new SimpleDateFormat("ddMMyyyy");
  SimpleDateFormat idf = new SimpleDateFormat("yyyy-MM-dd");
  NumberFormat usnf = NumberFormat.getInstance(Locale.US);
  int rsaCount; //number of RSA counted
  int ruCount; //number of RU counted
  
  static class Arguments {
    String[] args; int p = 0;
    public Arguments(String[] argArray) { args = argArray; }
    public boolean hasMoreArguments() { return p < args.length; }
    public String currentArgument() { return args[p]; }
    public String nextArgument() { return args[p++]; }
  }


  /**  
   * Constructeur simple
   */
  public Rsa2Csv() {}
  
  /**
   * Méthode triviale pour tester si une chaîne est vide.
   * @param str La chaîne à tester
   * @return true si la chaîne est vide
   */
  public static final boolean isEmpty(String str) {
    return str == null || str.trim().equals("");
  }
  
  /**
   * Initialisation avec des arguments.
   * <dl>
   * <dt>-in &lt;fichier_entree&gt;</dt>
   *   <dd>Fichier qui contient le RSA au format ATIH</dd>
   * <dt>-outdir &lt;repertoire_de_sortie&gt;</dt>
   *   <dd>répertoire qui contiendra les fichiers .csv</dd>
   * <dt>-enc &lt;encodage&gt;</dt>
   *   <dd>définit l'encodage de sortie<dd/>
   * <dt>-prefix &lt;préfixe&gt;</dt>
   *   <dd>Donne le préfixe que porteront tous les fichiers .csv en sortie<dd/>
   * <dt>-debug</dt>
   *   <dd>Active le mode débogage</dd>
   * </dl>
   * Par défaut l'encodage est "UTF-8" et le préfixe est la date courante, au format "yyMMddHHmm".
   * @param argsp Les arguments d'initialisation
   * @throws Exception _
   */
  void init(String[] argsp)
    throws Exception
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
      else if (arg.equals("-prefix")) {
        prefix = mandatory(args, "Argument manquant pour "+arg);
      }
      else if (arg.equals("-debug")) {
        Log4j2Utils.changeRootLogLevel(Level.DEBUG);
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
  
  void run()
      throws FieldParseException, IOException, ParseException, MissingMetafileException
  {
	int linenr = 1;
    RsaReader rrdr = new RsaReader();
    RsCsvHelper rh = new RsCsvHelper();
    rh.outPrefix = prefix;
    rh.outDir = new File(outDir);
    rh.makeStreams("SLI");
    rh.makeStreams("RSA");
    rh.makeStreams("AG");
    rh.makeStreams("SR");
    rh.makeStreams("RU");
    rh.makeStreams("DA");
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
          FszNode g = rrdr.readRSA(line, linenr);
          emitCsv((FszGroup)g, rh, line, linenr);
          rh.advanceIdCounter();
        }
        line = br.readLine(); linenr++;
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
      bw.write(String.valueOf(rsaCount));
      bw.write(';');
      bw.write(String.valueOf(ruCount));
      bw.newLine();
    }
    finally {
      fis.close();
      rh.closeStreams();
    }
  }

  /**
   * Emet les colonnes initiales de l'en-ête, selon le groupe qui est émis.<br>
   * Pour les fichiers RSA.csv, AG.csv, SR.csv, RU.csv, DA.csv, ZA.csv, le premier champ émis 
   * est une clé, qui commence par K, et le deuxième champ émis est la clé du parent
   * qui contient cet enregistrement.
   * @param gm La définition de groupe
   * @param bw Le {@link BufferedWriter} dans lequel ces informations seront écrites
   * @throws IOException Si erreur E/S
   */
  public void emitCsvHeader(FszGroupMeta gm, BufferedWriter bw)
      throws IOException 
  {
    String name = gm.getStdName();
    if (name.equals("SLI")) {
      bw.write("SLI_ID;IMPORT_DA;N_RSA;N_RU");
    }
    else if (name.equals("RSA")) {
      bw.write("RSA_ID;SLI_ID"); //ID + Local Session's Import ID
    }
    else if (name.equals("AG")) {
      bw.write("AG_ID;RSA_ID");
    }
    else if (name.equals("SR")) {
      bw.write("SR_ID;RSA_ID");
    }
    else if (name.equals("RU")) {
      bw.write("RU_ID;RSA_ID");
    }
    else if (name.equals("DA")) {
      bw.write("DA_ID;RU_ID");
    }
    else if (name.equals("ZA")) {
      bw.write("ZA_ID;RU_ID");
    }
    lg.debug("Writing header for "+name);
    List<FszMeta> cms = gm.getChildMetas();
    for (FszMeta cm : cms) {
      if (cm.isFieldMeta()) {
        bw.write(";");
        bw.write(cm.getStdName());
      }
    }//for
    if (name.equals("RSA")) bw.write(";LIGNERSA");
    bw.newLine();
  }
  
  /**
   * Envoyer les informations du groupe, dans les différents flux de .csv<br>
   * S'occupe d'appeler {@link #emitCsvHeader(FszGroupMeta, BufferedWriter)} si {@link RsCsvHelper#isHeaderEmitted(String)} est false.
   * @param g Le groupe à émettre
   * @param h L'objet d'assistance de sortie
   * @param line La ligne qui contient le RSA
   * @param linenr Le numéro de ligne 
   * @throws IOException Si erreur E/S
   * @throws ParseException Si erreur d'analyse
   */
  public void emitCsv(FszGroup g, RsCsvHelper h, String line, int linenr)
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
          if (groupName.equals("RSA")) {
            bw.write(";K"+h.getLsiId());
            h.setPId(h.getCurrentId());
            rsaCount++;
          }
          else if (groupName.equals("RU")) {
            h.setRuId(h.getCurrentId());
            bw.write(";K"+h.getPId()); i++;
            ruCount++;
          }
          else if (groupName.equals("DA") || groupName.equals("ZA")) {
            //parent of DA and ZA is RU
            bw.write(";K"+h.getRuId()); i++;           
          }
          else {
            //for AG*, SR*
            bw.write(";K"+h.getPId()); i++;
          }
        }
        String v = convertValue(childField);
        lg.debug(i+"::"+fm.getStdName()+" ["+linenr+","+childField.pos+"], value : "+v);
        bw.write(";"+v);
        i++;
      }
      else {
        h.advanceIdCounter();
        //c'est une métadonnée de groupe, appeler emitCsv récursivement
        emitCsv((FszGroup)child, h, line, linenr);
      }
    }
    if (gm.getGroupName().equals("RSA")) bw.write(";"+line);
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
      if (isEmpty(value)) return value;
      else return idf.format(edf.parse(value));
    }
    if (fld.representsNumber()) {
      String value = fld.getValue();
      if (isEmpty(value)) return value;
      lg.debug("Parsing ["+fld.pos+"] '"+value+"'...");
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
   * Méthode principale.
   * Crée un objet {@link Rsa2Csv}, appelle {@link Rsa2Csv#init(String[])} avec les arguments, 
   * puis enfin appelle {@link Rsa2Csv#run()}
   * @param args Les arguments
   * @throws Exception _
   */
  public static void main(String[] args)
      throws Exception 
  {
    //BasicConfigurator.configure();
    //Logger.getRootLogger().setLevel(Level.INFO);
    Rsa2Csv app = new Rsa2Csv();
    app.init(args);
    app.run();
  }

}
