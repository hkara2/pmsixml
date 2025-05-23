package fr.gpmsi.pmsixml.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

//import static org.junit.Assert.assertEquals;

import java.io.IOException;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FieldParseException;
import fr.gpmsi.pmsixml.FszGroup;
import fr.gpmsi.pmsixml.FszNode;
import fr.gpmsi.pmsixml.MissingMetafileException;
import fr.gpmsi.pmsixml.RssReader;

/**
 * Tests de lecture des RSS
 */
public class RssReaderTest {

  /**
   * RSS de test 1, au format 17 groupé (117)
   */
  public static final String RSS1_117 = "1106M133 11700091001944701700000000000003456001015581960           1         0412194516112  1409201571191120156191510000000        001000012C155            000                                 D648    E1198   E440    F172    Z290    J432    K860    R13     R634    J42     14092015ZBQK002   01 Z   0 0115102015DZQM006   01     0 0123092015ZBQK002   01 BZ  0 0116092015HZHE002   01     0 0105102015HJQE002   01     0 0116092015YYYY600   01     010102102015GEQE007   01 U   0 0122102015DZQM006   01     0 0116092015ZZQP004   01     010117112015JAQM003   01     0 0116092015HEQE002   01 U   0 0116092015ZZQH033   01 Z   0101";
  /**
   * RSS de test 2, au format 15 groupé (115)
   */
  public static final String RSS2_115 = "1114Z02A 11500091001944701500000000000001681196006495347           220       2604199024201  010120118 040120118 45300304040000200003O680            000              Z370    Z391    01012011JMPA00601     0 0101012011JQGD00601     0 0101012011JQGD01001 S   0 01";
  
  /**
   * Activation du débogate (par défaut false)
   */
  public static final boolean DEBUG = false;
  
  /**
   * Préparation (initialise les logs)
   * @throws Exception _
   */
  @BeforeEach
  public void setUp() throws Exception {
    //BasicConfigurator.configure();
	  Configurator.initialize(new DefaultConfiguration());
  }

  /**
   * Teste la lecture du RSS 1 et de l'analyse de sa structure
   * @throws Exception _
   */
  @Test
  public void testReadRssForStructure()
      throws Exception 
  {
    RssReader app = new RssReader();
    String rss = RSS1_117;
    FszGroup gn = (FszGroup) app.readOne(rss);
    String nadl = gn.getChildField("NADL").getValue();
    
    if (DEBUG) System.out.println("'"+nadl+"'");
    assertEquals("015581960", nadl.trim(), "Expected 015581960");
  }

  /**
   * Tests l'analyse du RSS 2 et de la lecture du champ VRSS (qui
   * doit avoir la valeur 115).
   * @throws Exception _
   */
  @Test
  public void testOneRSS2_115()
      throws Exception
  {
    RssReader app = new RssReader();
    String rss = RSS2_115;
    FszGroup gn = (FszGroup) app.readOne(rss);
    Integer vrss = gn.getChildField("VRSS").toInt();
    
    System.out.println("'"+vrss+"'");
    assertEquals(Integer.valueOf(115), vrss, "Expected 115");    
  }
  
  /**
   * Test de lecture du NADL (à écrire) 
   * @throws Exception _
   */
  @Test
  public void testReadNadl() throws Exception {
    
  }
  
  /**
   * Test de lecture d'un petit RSS
   * @throws FieldParseException Si erreur lors de l'analyse d'un champ
   * @throws IOException Si erreur d'E/S
   * @throws MissingMetafileException Si il manque un fichier de définition
   */
  @Test
  public void testShort()
		throws FieldParseException, IOException, MissingMetafileException
  {
      RssReader app = new RssReader();
      String rss = "1106M133 11700091001944701700000000000003456001015581960           1         0412194516112  1409201571191120156191510000000        001000012C155            000                                 D648    E1198   E440    F172    Z290    J432    K860    R13     R634    J42     14092015ZBQK002   01 Z   0 0115102015DZQM006   01     0 0123092015ZBQK002   01 BZ  0 0116092015HZHE002   01     0 0105102015HJQE002   01     0 0116092015YYYY600   01     010102102015GEQE007   01 U   0 0122102015DZQM006   01     0 0116092015ZZQP004   01     010117112015JAQM003   01     0 0116092015HEQE002   01 U   0 0116092015ZZQH033   01 Z   0101";
      FszNode nd = app.readOne(rss);
      StringBuffer sb = new StringBuffer();
      nd.dump(sb, 0);
      if (DEBUG) System.out.println(""+sb);
  }
    
  /**
   * Test d'un RSS très très long (qui contient beaucoup d'actes)
   * @throws Exception _
   */
    @Test
    public void testLong()
            throws Exception
    {
      RssReader app = new RssReader();
      String rssLong = "1106C044 1171709100194470170000000000000357056601608237"
          + "6           4         1109193524030  0801201661280220169 9141000000"
          + "0        001100273K650            06"
          + "6                                 Y654    N170    R572    J958    J"
          + "151    I455    J960    E43     D648    I442    U83710  18012016HSLF"
          + "001   01     0 0130012016YYYY020   01     010125012016YYYY020   0"
          + "1     010107022016YYYY020   01     010110022016GLLD015   01     0"
          + "20127012016GLLD015   01     020110022016FELF004   01     0 012202"
          + "2016YYYY020   01     010102022016HSLF001   01     0 0114012016ENL"
          + "F001   01     020102022016ZBQK002   01 BZ  010120012016ENLF001   0"
          + "1     020105022016GLLD015   01     020127012016YYYY020   01     0"
          + "10120022016YYYY020   01     010108012016YYYY020   01     0 011202"
          + "2016HSLF001   01     020104022016GLLD008   01     020117022016JV"
          + "JF002   01     010129012016EJQM003   01     010118022016FELF00"
          + "4   01     0 0107022016GLLD015   01     020110022016HSLF001   0"
          + "1     0 0120022016GLLD008   01     0 0126012016ZBQK002   01 BZ  0"
          + "10130012016GLLD015   01     020112012016ENLF001   01     0201240"
          + "12016HSLF001   01     0 0114022016YYYY020   01     010108012016"
          + "GLLD008   01     0 0118022016DZQM005   01     010118012016ENLF"
          + "001   01     020119022016ENLF001   01     0 0123022016ENLF00"
          + "1   01     0 0112022016GLLD015   01     0 0123022016JVJF00"
          + "2   01     010110012016YYYY020   01     010102022016YYYY02"
          + "0   01     010118022016ENLF001   01     0 0121012016ENLF00"
          + "1   01     020120022016GGJB001   01     010125022016HSLF00"
          + "1   01     020102022016JVJF002   01     010105022016HEQE00"
          + "2   01     010125022016ENLF001   01     0 0117022016EQLF00"
          + "3   01     0 0127012016DEQP003   01     010112022016EJQM00"
          + "3   01     010111022016ZBQK002   01 B   010116022016GLLD01"
          + "5   01     0 0102022016GEQE007   01     010113012016GEQE00"
          + "7   01     0 0128012016YYYY020   01     010112012016YYYY02"
          + "0   01     010113012016HSLF002   01     0 0117012016HSLF00"
          + "1   01     0 0104022016YYYY020   01     010121012016GLLD01"
          + "5   01     020127022016HSLF001   01     020107022016HSLF00"
          + "1   01     0 0121022016JVJF002   01     010110022016YYYY02"
          + "0   01     010129012016YYYY020   01     010119022016YYYY02"
          + "0   01     010131012016HSLF001   01     0 0104022016HSLF00"
          + "1   01     0 0108022016ZBQK002   01 B   010108012016EQLF00"
          + "2   01     0 0119012016GLLD015   01     020102022016GLLD01"
          + "5   01     020102022016EQLF002   01     0 0119022016GLLD00"
          + "8   01     0 0110012016HSLF002   01     0 0123012016ENLF00"
          + "1   01     020111022016JVJF002   01     010118022016JVJF00"
          + "2   01     010123022016GLLD015   01     0 0101022016ZBQK00"
          + "2   01 BZ  010120022016ENLF001   01     0 0105022016ZBQK00"
          + "2   01 BUZ 010114012016GEQE007   01     0 0108012016JVJF00"
          + "2   01     0 0103022016YYYY020   01     010113012016GLLD01"
          + "5   01     020119022016ZBQK002   01 B   010113012016ZBQK00"
          + "2   01 BZ  0 0105022016ZZQK024   01     040106022016YYYY02"
          + "0   01     010128022016HSLF001   01     020108012016EQLF00"
          + "3   01     0 0124012016GLLD015   01     020106022016GLLD01"
          + "5   01     020108012016ENLF001   01     0 0108012016FELF00"
          + "4   01     0 0127012016JAQM003   01     010117012016JVJF00"
          + "2   01     0 0125022016JVJF002   01     010114022016JVJF00"
          + "2   01     010117022016GLLD008   01     0 0131012016GLLD01"
          + "5   01     020118012016JVJF002   01     0 0121012016YYYY02"
          + "0   01     010116012016GLLD015   01     020122022016ENLF00"
          + "1   01     0 0103022016JVJF002   01     010110012016GLLD00"
          + "8   01     020128012016ZBQK002   01 BZ  010116012016ENLF00"
          + "1   01     020127012016ZBQK002   01 BZ  010126012016DEQP00"
          + "3   01     0 0130012016HSLF001   01     0 0120012016JVJF00"
          + "2   01     0 0117012016YYYY020   01     010123012016YYYY02"
          + "0   01     010129012016HSLF001   01     0 0121022016HSLF00"
          + "1   01     020118012016GLLD015   01     020117012016GLLD01"
          + "5   01     020119022016GGJB001   01     010121012016HSLF00"
          + "1   01     0 0106022016JVJF002   01     010109012016YYYY02"
          + "0   01     010112022016ZBQK002   01 BZ  010118022016ZZQH03"
          + "3   01 Z   040126012016YYYY020   01     010109022016GLLD01"
          + "5   01     020122012016HSLF001   01     0 0127022016YYYY02"
          + "0   01     010124012016YYYY020   01     010126012016ZBQK00"
          + "2   01 BZ  0 0129012016GLLD015   01     020125012016ENLF00"
          + "1   01     020101022016YYYY020   01     010121022016EQLF00"
          + "3   01     0 0118012016YYYY020   01     010118022016YYYY02"
          + "0   01     010109012016EQLF003   01     0 0125012016JVJF00"
          + "2   01     010114012016ZBQK002   01 BZ  0 0109012016GLLD00"
          + "8   01     020115012016ENLF001   01     020115022016HSLF00"
          + "1   01     020105022016JVJF002   01     010108022016YYYY02"
          + "0   01     010126022016YYYY020   01     010131012016FELF00"
          + "4   01     0 0117022016GEQE007   01     010125022016YYYY02"
          + "0   01     010123022016HSLF001   01     020114022016GLLD01"
          + "5   01     0 0110012016ZBQK002   01 BFZ 0 0105022016ZBQK00"
          + "2   01 BZ  010109012016ZBQK002   01 BZ  0 0105022016YYYY02"
          + "0   01     010121022016GGJB001   01     010111022016GLLD01"
          + "5   01     020111012016GLLD015   01     020115012016HSLF00"
          + "1   01     0 0111022016HSLF001   01     0 0113022016HSLF00"
          + "1   01     020123012016JVJF002   01     010124022016ENLF00"
          + "1   01     0 0128012016GLLD015   01     020123012016GLLD01"
          + "5   01     020126012016HSLF001   01     0 0116012016JVJF00"
          + "2   01     0 0115012016JVJF002   01     0 0128022016YYYY02"
          + "0   01     010120012016GLLD015   01     020115022016YYYY02"
          + "0   01     010131012016YYYY020   01     010121022016ENLF00"
          + "1   01     0 0109022016HSLF001   01     0 0106022016HSLF00"
          + "1   01     0 0118022016GLLD008   01     0 0130012016JVJF00"
          + "2   01     010111012016HSLF002   01     0 0110012016ZBQK00"
          + "2   01 BUZ 0 0110012016GLLD017   01     0 0111012016GLLD01"
          + "7   01     0 0114012016GLLD015   01     020122022016HSLF00"
          + "1   01     020125012016GLLD015   01     020128022016GLLD01"
          + "5   01     0 0115012016GLLD015   01     020126022016HSLF00"
          + "1   01     020117022016HSLF001   01     020117012016ENLF00"
          + "1   01     020115022016JVJF002   01     010112012016GLLD01"
          + "7   01     0 0101022016GLLD015   01     020126012016GLLD01"
          + "5   01     020113012016GLLD017   01     0 0123012016HSLF00"
          + "1   01     0 0128012016HSLF001   01     0 0104022016JVJF00"
          + "2   01     010121012016ZBQK002   01 BUZ 0 0117022016YYYY02"
          + "0   01     010112022016YYYY020   01     010128022016ENLF00"
          + "1   01     0 0122022016GLLD008   01     0 0125022016GLLD01"
          + "5   01     0 0124012016ENLF001   01     020125012016HSLF00"
          + "1   01     0 0127022016GLLD015   01     0 0116022016HSLF00"
          + "1   01     020121022016YYYY020   01     010115012016YYYY02"
          + "0   01     010105022016YYYY600   01     040116012016YYYY02"
          + "0   01     010123022016YYYY020   01     010119012016ENLF00"
          + "1   01     020109012016ENLF001   01     020104022016FELF00"
          + "4   01     0 0112012016JVJF002   01     0 0120012016YYYY02"
          + "0   01     010114012016YYYY020   01     010110022016ZBQK00"
          + "2   01 BUZ 010117022016ENLF001   01     0 0101022016HSLF00"
          + "1   01     0 0110012016ENLF001   01     020109022016YYYY02"
          + "0   01     010119012016YYYY020   01     010113012016YYYY02"
          + "0   01     010113012016ENLF001   01     020124022016HSLF00"
          + "1   01     020127022016ENLF001   01     0 0116022016YYYY02"
          + "0   01     010113022016GLLD015   01     0 0101022016JVJF00"
          + "2   01     010118022016EQLF003   01     0 0126022016GLLD01"
          + "5   01     0 0114012016JVJF002   01     0 0117022016EQLF00"
          + "2   01     010117012016FELF011   01     0 0120022016HSLF00"
          + "1   01     020115022016GLLD015   01     0 0112022016EQLF00"
          + "2   01     010105022016HSLF001   01     0 0108022016GLLD01"
          + "5   01     020114022016HSLF001   01     020126012016GLLD01"
          + "2   01     0 0112012016HSLF002   01     0 0111012016DEQP00"
          + "3   01     0 0111022016YYYY020   01     010103022016GLLD01"
          + "5   01     020108022016DEQP003   01     010103022016HSLF00"
          + "1   01     0 0120022016ZBQK002   01 B   010113022016YYYY02"
          + "0   01     010122012016YYYY020   01     010121022016GLLD00"
          + "8   01     0 0117022016FELF003   01     0 0118022016YYYY60"
          + "0   01     040108022016HSLF001   01     0 0118022016EQLF00"
          + "2   01     010122012016ENLF001   01     020126022016ENLF00"
          + "1   01     0 0124022016GLLD015   01     0 0119012016HSLF00"
          + "1   01     0 0104022016DEQP003   01     010110012016JVJF00"
          + "2   01     0 0110022016JVJF002   01     010124022016YYYY02"
          + "0   01     010111012016ENLF001   01     020105022016EQLF00"
          + "2   01     0 0120022016EQLF003   01     0 0120012016HSLF00"
          + "1   01     0 0122012016GLLD015   01     020114012016HSLF00"
          + "2   01     0 0127012016HSLF001   01     0 0119022016EQLF00"
          + "3   01     0 0116012016HSLF001   01     0 01";
      FszNode nd = app.readOne(rssLong);
      StringBuffer sb = new StringBuffer();
      nd = app.readOne(rssLong);
      sb = new StringBuffer();
      nd.dump(sb, 0);
      if (DEBUG) System.out.println(""+sb);
  }
	

}
