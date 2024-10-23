package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
//import org.apache.log4j.BasicConfigurator;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszGroup;
import fr.gpmsi.pmsixml.FszGroupMeta;
import fr.gpmsi.pmsixml.MetaFileLoader;
import fr.gpmsi.pmsixml.RhsReader;

/**
 * Tests pour les Groupes de champs
 */
public class FszGroupTest {

  /**
   * Préparation
   * @throws Exception _
   */
  @BeforeEach
  public void setUp() throws Exception {
	Configurator.initialize(new DefaultConfiguration());
    //BasicConfigurator.configure();
  }

  /**
   * Tester la copie de noeud
   * @throws Exception _
   */
  @Test
  public void testNodeCopy()
      throws Exception 
  {
    String rhsStr = "          M1A910019447M0A2096   018564888           059096Z0151 28042017        2611196129158012804201771          2720171111111611750A        Z5188   G931            44444405002002   Z741    F411    G408    R2630   F323    ALT+145         30 01030720171 1   ALT+145         30 01050720171 1   ";
    StringBuffer sb = new StringBuffer();
    MetaFileLoader mfl = new MetaFileLoader();
    FszGroupMeta fgm = mfl.loadMeta("/fr/gpmsi/pmsixml/rhsm1b.csv");
    RhsReader rrd = new RhsReader();
    FszGroup rhs = (FszGroup) rrd.readOne(rhsStr);
    FszGroup dst = fgm.getFirstChildGroupMeta().makeBlankInstance();
    sb.append("dst vierge : \n");
    dst.dump(sb, 5);
    rhs.copyFieldsTo(dst);
    sb.append("structure meta src : \n");
    rhs.getMeta().dump(sb);
    sb.append("structure meta dst : \n");
    fgm.dump(sb);
    sb.append("rhs version 1A :\n");
    rhs.dump(sb);
    sb.append("rhs version 1B :\n");
    dst.dump(sb);
    System.out.println(""+sb);
  }

}
