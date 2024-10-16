package fr.gpmsi.pmsixml;

/**
 * Test simple pour vérifier que cela se lance bien.
 */
public class Rhs2CsvTest
{

  /**
   * Constructeur simple
   */
  public Rhs2CsvTest() {
  }

  private void test1() throws Exception {
    String[] args = {"-in", "D:\\devtests\\pmsixml\\rums2016.txt", "-prefix", "rums2016", "-debug", "-outdir", "D:\\devtests\\pmsixml"};
    Rhs2Csv.main(args);
  }
  
  /**
   * Lancement du test
   * @throws Exception _
   */
  public void run() throws Exception {
    test1();
  }
  
  /**
   * Méthode main() qui crée un {@link Rhs2CsvTest} puis appelle {@link #run()}
   * @param args Les arguments
   * @throws Exception _
   */
  public static void main(String[] args) throws Exception {
    Rhs2CsvTest app = new Rhs2CsvTest();
    app.run();
  }

}
