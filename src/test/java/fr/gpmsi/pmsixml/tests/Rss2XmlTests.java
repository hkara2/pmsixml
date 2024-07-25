package fr.gpmsi.pmsixml.tests;

import java.io.File;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.Rss2Xml;

public class Rss2XmlTests {

	@BeforeEach
	public void setUp() {
		//BasicConfigurator.configure();
		Configurator.initialize(new DefaultConfiguration());
	}

	@Test
	public void test1()
			throws Exception 
	{
		String[] args = new String[] { "-in", "files-for-tests\\in\\t01rss017.txt", "-out", "files-for-tests\\tmp-out\\t01rss017.xml" };
		new File("files-for-tests/tmp-out").mkdirs();
		Rss2Xml.main(args);
	}
}
