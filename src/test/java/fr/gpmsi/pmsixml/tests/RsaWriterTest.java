package fr.gpmsi.pmsixml.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FieldSizeException;
import fr.gpmsi.pmsixml.FszField;
import fr.gpmsi.pmsixml.FszGroup;
import fr.gpmsi.pmsixml.RsaReader;
import fr.gpmsi.pmsixml.RsaWriter;

public class RsaWriterTest {

	public static String rsa1 = RsaReaderTest.rsa1;
	public static String rsa2 = RsaReaderTest.rsa2;
	
	/** @apiNote Si true des messages sont imprimes en plus */
	public static boolean DEBUG = false;
	
	@Test
	public void test1()
			throws Exception
	{
		StringBuffer sb = new StringBuffer();
		RsaReader rsar = new RsaReader();
		
		FszGroup rsa = rsar.readRSA(rsa1, 1);
		RsaWriter rsaw = new RsaWriter();
		rsaw.writeRsa(rsa, sb);
		if (DEBUG) {
			System.out.println("Original  :"+rsa1);
			System.out.println("Rewritten :"+sb);
			System.out.println();
		}
		
		sb.setLength(0);
		rsa = rsar.readRSA(rsa2, 1);
		rsaw.writeRsa(rsa, sb);
		if (DEBUG) {
			System.out.println("Original  :"+rsa2);
			System.out.println("Rewritten :"+sb);
			System.out.println();
		}
	}
	
	/**
	 * try to modify something with an invalid length
	 * hk 170131 ok throws error
	 * @throws Exception _
	 */
	@Test
	public void test2()
			throws Exception
	{
		StringBuffer sb = new StringBuffer();
		RsaReader rsar = new RsaReader();
		
		FszGroup rsa = rsar.readRSA(rsa1, 1);
		//FszGroup p = rsa.getChildGroup("P");
		//FszField pegr = p.getChildField("PEGR");
		FszField pegr = rsa.getChildField("PEGR");
		pegr.setValue("10012");
		RsaWriter rsaw = new RsaWriter();
		assertThrows(FieldSizeException.class, () -> rsaw.writeRsa(rsa, sb));
		
		if (DEBUG) {
			System.out.println("Original  :"+rsa1);
			System.out.println("Rewritten :"+sb);
			System.out.println();					
		}
	}
	
	public RsaWriterTest() {
	}

	public static void main(String[] args) throws Exception {
		//BasicConfigurator.configure();
		Configurator.initialize(new DefaultConfiguration());
		RsaWriterTest app = new RsaWriterTest();
		app.test1();
	}

}
