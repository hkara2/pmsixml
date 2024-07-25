package fr.gpmsi.pmsixml.tests;

import java.io.File;
import java.io.IOException;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import fr.gpmsi.pmsixml.FieldParseException;
import fr.gpmsi.pmsixml.FszGroup;
import fr.gpmsi.pmsixml.MissingMetafileException;
import fr.gpmsi.pmsixml.MonoLevelReader;

public class MonoLevelReaderTest {

  public static void main(String[] args)
      throws FieldParseException, IOException, MissingMetafileException
  {
    //BasicConfigurator.configure();
    Configurator.initialize(new DefaultConfiguration());
    @SuppressWarnings("unused")
    String traMetaFilePath = "C:\\Local\\e-pmsi\\exports\\MCO\\2016\\LAMDA\\170308\\tra2016.csv";
    String metasDirPath = "C:\\Local\\e-pmsi\\exports\\MCO\\2016\\LAMDA\\170308";
    String testLine = "0000000010000000000000035500110000000031015992499           0712201511K02418012016";
    MonoLevelReader rdr = new MonoLevelReader();
    rdr.setMetasDir(new File(metasDirPath));
    rdr.setMetaName("tra2016");
    FszGroup g = rdr.readMonoLevel(testLine, 0);
    System.out.println("g:"+g);
  }

}
