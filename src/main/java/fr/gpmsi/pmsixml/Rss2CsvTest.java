package fr.gpmsi.pmsixml;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

/** Classe de test */
public class Rss2CsvTest
{

  /** constructeur simple */
  public Rss2CsvTest() {
  }

  private void test1()
  		throws Exception 
  {
    String[] args = {"-in", "D:\\devtests\\pmsixml\\rums2016.txt", "-prefix", "rums2016", "-outdir", "D:\\devtests\\pmsixml"};
    Rss2Csv.main(args);
  }
  
  /**
   * exécution
   * @throws Exception Si erreur
   */
  public void run() throws Exception {
    test1();
  }
  
  /**
   * Lancement en tant qu'application
   * @param args Paramètres en ligne de commande
   * @throws Exception Si erreur lors de l'exécution
   */
  public static void main(String[] args) throws Exception {
  	//BasicConfigurator.configure();
  	Configurator.initialize(new DefaultConfiguration());
  	//Logger.getRootLogger().setLevel(Level.INFO);
    Rss2CsvTest app = new Rss2CsvTest();
    app.run();
  }

}
