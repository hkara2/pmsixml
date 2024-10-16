package fr.gpmsi.pmsixml.tests;

import java.io.File;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
//import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.Fsz2Xml;

/**
 * Tests
 */
public class Fsz2XmlTests /*extends TestCase*/ {

	/**
	 * Constructeur simple
	 */
	public Fsz2XmlTests() {
		//BasicConfigurator.configure();
		Configurator.initialize(new DefaultConfiguration());
	}

	//@Test
	//public Fsz2XmlTests(String name) {
	//	super(name);
	//	//BasicConfigurator.configure();
	//	Configurator.initialize(new DefaultConfiguration());
	//}

	/**
	 * Test simple avec un rss de test t01rss017.txt
	 * @throws Exception
	 */
	@Test
	public void test1()
			throws Exception 
	{
		String[] args = new String[] { "-in", "files-for-tests\\in\\t01rss017.txt", "-out", "files-for-tests\\tmp-out\\t01rss017_b.xml", "-m", "rss017" };
		new File("files-for-tests/tmp-out").mkdirs();
		Fsz2Xml.main(args);
	}
	
	/**
	 * Test simple avec un vidhosp de test t01vhV011.txt 
	 * @throws Exception
	 */
	@Test
    public void test2()
        throws Exception 
    {
        String[] args = new String[] { "-in", "files-for-tests\\in\\t01vhV011.txt", "-out", "files-for-tests\\tmp-out\\t01vhV011_b.xml", "-m", "vidhospV011" };
        new File("files-for-tests/tmp-out").mkdirs();
        Fsz2Xml.main(args);
    }
	
}
