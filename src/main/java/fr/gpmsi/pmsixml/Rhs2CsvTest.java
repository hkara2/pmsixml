package fr.gpmsi.pmsixml;

public class Rhs2CsvTest
{

  public Rhs2CsvTest() {
  }

  private void test1() throws Exception {
    String[] args = {"-in", "D:\\devtests\\pmsixml\\rums2016.txt", "-prefix", "rums2016", "-debug", "-outdir", "D:\\devtests\\pmsixml"};
    Rhs2Csv.main(args);
  }
  
  public void run() throws Exception {
    test1();
  }
  
  public static void main(String[] args) throws Exception {
    Rhs2CsvTest app = new Rhs2CsvTest();
    app.run();
  }

}
