package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszGroupMeta;
import fr.gpmsi.pmsixml.MetaFileLoader;

/**
 * Tests de la lecture des métadonnées de groupe.
 */
public class RssGroupMetaTest {

    /**
     * Préparation, initialise juste les logs
     * @throws Exception _
     */
  @BeforeEach
  public void setUp() throws Exception {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
  }

  /**
   * Teste la lecture de métadonnées de RSS de type 017 (format 17 non groupé)
   * @throws Exception _
   */
  @Test
  public void testReadRssMeta() 
      throws Exception
  {
    MetaFileLoader mfl = new MetaFileLoader();
    FszGroupMeta fgm = mfl.loadMeta("/rss017.csv");
    StringBuffer sb = new StringBuffer();
    fgm.dump(sb);
    System.out.println(""+sb);
  }

}
