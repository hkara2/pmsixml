package fr.gpmsi.pmsixml;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

public class Rss2CsvTest
{

  public Rss2CsvTest() {
  }

  private void test1()
  		throws Exception 
  {
    String[] args = {"-in", "D:\\devtests\\pmsixml\\rums2016.txt", "-prefix", "rums2016", "-outdir", "D:\\devtests\\pmsixml"};
    Rss2Csv.main(args);
  }
  
  public void run() throws Exception {
    test1();
  }
  
  public static void main(String[] args) throws Exception {
  	//BasicConfigurator.configure();
  	Configurator.initialize(new DefaultConfiguration());
  	//Logger.getRootLogger().setLevel(Level.INFO);
    Rss2CsvTest app = new Rss2CsvTest();
    app.run();
  }

}
