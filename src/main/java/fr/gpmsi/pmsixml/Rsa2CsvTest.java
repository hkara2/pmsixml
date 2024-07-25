package fr.gpmsi.pmsixml;

public class Rsa2CsvTest
{

  public Rsa2CsvTest() {
  }

  private void test1() throws Exception {
    String args[] = { "-in", "D:\\devtests\\pmsixml\\910019447.2015.12.rsa", "-outdir", "D:\\devtests\\pmsixml", "-prefix", "rsa_" 
        //, "-debug"
        };
    Rsa2Csv.main(args);
  }
  
  public static void main(String[] args) throws Exception {
    Rsa2CsvTest app = new Rsa2CsvTest();
    app.test1();
  }

}
