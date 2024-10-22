package fr.gpmsi.pmsixml.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszGroup;
import fr.gpmsi.pmsixml.FszNode;
import fr.gpmsi.pmsixml.RhsReader;
import fr.gpmsi.pmsixml.RhsWriter;

/**
 * Test d'écriture des RHS
 */
public class RhsWriterTest {

  /**
   * Préparation, initialise les logs
   * @throws Exception _
   */
  @BeforeEach
  public void setUp() throws Exception {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
  }

  /**
   * Fait un test de lecture puis d'écriture d'un RHS au format M9 non
   * groupé (M09)
   * @throws Exception _
   */
  @Test
  public void testRhsReadWrite1()
      throws Exception
  {
    //RHS de test, format M9 non groupé
    String rhsTxt = "910019447M093687123016584352                       0404201617042017261119612915801040420167117042017711220171111111611752A        Z5188   G931    F09     4444440500000 F322    G408    F411    R2630   Z741    ";
    RhsReader rr = new RhsReader();
    FszNode nd = rr.readOne(rhsTxt);
    StringBuffer sb = new StringBuffer();
    RhsWriter rw = new RhsWriter();
    rw.writeRhs((FszGroup)nd, sb);
    System.out.println(rhsTxt);
    System.out.println(""+sb);
    assertEquals(rhsTxt, sb.toString(), "Les deux chaines doivent etre identiques");
  }

  /**
   * Fait un test de lecture puis d'écriture d'un RHS au format A groupé
   * (M1A)
   * @throws Exception _
   */
  @Test
  public void testRhsReadWrite2()  
      throws Exception
  {
    //RHS de test, format MA groupé, très long
    String rhsTxt = "          M1A910019447M0A2846   850002310           050833D1000 07052018140620182407193119167010705201861          2220181111111460059A        Z508    Z966    S7230   44221106027002   Z950    E43     E559    Z741    Z740    Z966    PCM+262         22 01280520187 1   PEQ+038         10 01290520181 1   QZQ+104         10 01300520181 1   ZGT+031         10 01300520181 1   NKR+117         22 01280520181 1   NKR+117         22 01300520181 1   ANQ+126         10 01310520181 1   ZGQ+294         10 01290520181 1   ZZC+221         10 01290520181 1   NKR+204         22 01300520181 1   ANQ+126         10 01300520181 1   PCM+283         22 01010620181 1   NKR+117         22 01010620181 1   QZQ+104         10 01030620181 1   ANQ+126         10 01030620181 1   QZQ+104         10 01280520181 1   ANQ+126         10 01020620181 1   HSQ+107         10 01290520181 1   QZQ+104         10 01010620181 1   NKR+204         22 01290520181 1   ANQ+126         10 01280520181 1   NKR+117         22 01290520181 1   ANQ+126         10 01290520181 1   PCM+262         22 01310520187 1   ANQ+126         10 01010620181 1   ZGT+031         10 01290520181 1   NKR+117         22 01310520181 1   ";
    RhsReader rr = new RhsReader();
    FszNode nd = rr.readOne(rhsTxt);
    StringBuffer sb = new StringBuffer();
    RhsWriter rw = new RhsWriter();
    rw.writeRhs((FszGroup)nd, sb);
    System.out.println(rhsTxt);
    System.out.println(""+sb);
    assertEquals(rhsTxt, sb.toString(), "Les deux chaines doivent etre identiques");
  }

}
