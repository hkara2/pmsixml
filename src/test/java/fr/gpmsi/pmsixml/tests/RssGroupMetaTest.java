package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszGroupMeta;
import fr.gpmsi.pmsixml.MetaFileLoader;

public class RssGroupMetaTest {

  @BeforeEach
  public void setUp() throws Exception {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
  }

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
