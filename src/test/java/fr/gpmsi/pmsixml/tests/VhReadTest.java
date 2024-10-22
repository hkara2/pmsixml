package fr.gpmsi.pmsixml.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

//import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszField;
import fr.gpmsi.pmsixml.FszFieldMeta;
import fr.gpmsi.pmsixml.FszGroup;
import fr.gpmsi.pmsixml.FszReader;
import fr.gpmsi.pmsixml.InputString;
import fr.gpmsi.pmsixml.MetaFileLoader;

/**
 * Tests de lecture de VID-HOSP
 */
public class VhReadTest {

  /**
   * VIDHOSP de test 1
   */
  public static final String VH1_14  = "2880391471039590129020120222012345678           V0149100194472220191223003675R10  1 0000100000000000000008000000025523700000000255237100000900140521             020120220000000000 01234567810009110000                                                  2        02012022911000 0201202205012022000000000                                                999FRA999999023844                                                                                                     00";
  /**
   * VIDHOSP de test 2
   */
  public static final String VH1_14B = "2991299341198850189301219992012345679           V0149100194472991299341198855R30  1 0000100000000000000008000000033188400000000331884100000                      010920220000000000 01234567910009110000                                                  1                000000 0109202204092022000000000                                                999FRA000615909580        123456789012345                                                                              00";
  
  /**
   * Préparation (initialise les logs)
   * @throws Exception
   */
  @BeforeEach
  public void setUp() throws Exception {
    //BasicConfigurator.configure();
	  Configurator.initialize(new DefaultConfiguration());
  }

  /**
   * Teste la lecture du VIDHOSP 1
   * @throws Exception _
   */
  @Test
  public void testReadVh1()
      throws Exception 
  {
    //MonoLevelReader app = new MonoLevelReader();
    MetaFileLoader mfl = new MetaFileLoader();
    FszReader fszr = new FszReader(mfl, "vidhosp");
    String vh = VH1_14;
    InputString instr = new InputString(vh);
    FszGroup gn = (FszGroup) fszr.readOne(instr);
    String nadl = gn.getChildField("NADL").getValue();
    
    //S ystem.out.println("'"+nadl+"'");
    assertEquals("012345678", nadl.trim(), "Expected 012345678");
  }
  
  /**
   * Lire un VIDHOSP et voir si la lecture de différents champs fonctionne
   * @throws Exception _
   */
  @Test
  public void testReadVh2()
      throws Exception 
  {
    //MonoLevelReader app = new MonoLevelReader();
    MetaFileLoader mfl = new MetaFileLoader();
    FszReader fszr = new FszReader(mfl, "vidhosp");
    String vh = VH1_14B;
    InputString instr = new InputString(vh);
    FszGroup gn = (FszGroup) fszr.readOne(instr);
    FszField fld = gn.getChildField("INS");
    String mrc = fld.getValue(); //montant remboursable par la caisse, valeur brute
    BigDecimal insCorr = fld.getCorrectedValue();
    
    //S ystem.out.println("INS         : '"+mrc+"'");
    //S ystem.out.println("INS corrige : '"+insCorr+"'");
    String format = ((FszFieldMeta)fld.getMeta()).getFormat();
    //S ystem.out.println("INS format  : '"+format+"'");
    assertEquals("123456789012345", mrc, "Attendu pour la valeur brute 123456789012345");
    assertEquals(new BigDecimal("1234567890123.45"), insCorr, "Attendu pour la valeur corrigee 1234567890123.45");
    assertEquals("13+2", format, "Attendu pour le format 13+2");
  }
  
  /**
   * Test de lecture du NADL (à écrire)
   * @throws Exception _
   */
  @Test
  public void testReadNadl() throws Exception {
    
  }
}
