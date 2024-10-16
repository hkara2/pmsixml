package fr.gpmsi.pmsixml;

/**
 * Test très basique pour appeler {@link Rsa2Csv} et constater manuellement ce que ça a donné.
 */
public class Rsa2CsvTest
{

  /**
   * Constructeur simple
   */
  public Rsa2CsvTest() {
  }

  private void test1() throws Exception {
    String args[] = { "-in", "files-for-tests/rsa01/910019447.2017.0-cut.rsa", "-outdir", "files-for-tests/tmp-out", "-prefix", "testsrsa_" 
        //, "-debug"
        };
    Rsa2Csv.main(args);
  }

  /**
   * Méthode main, qui ne fait que l'appel du test
   * @param args ignoré
   * @throws Exception _
   */
  public static void main(String[] args) throws Exception {
    Rsa2CsvTest app = new Rsa2CsvTest();
    app.test1();
  }

}
