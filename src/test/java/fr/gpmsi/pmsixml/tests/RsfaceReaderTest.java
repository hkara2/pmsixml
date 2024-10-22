package fr.gpmsi.pmsixml.tests;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszNode;
import fr.gpmsi.pmsixml.RsfaceReader;

/**
 * Tests de lecture de RSF-ACE
 */
public class RsfaceReaderTest
{
	static Logger lg = LogManager.getLogger(RsfaceReaderTest.class);

	@BeforeEach
	protected void setUp() throws Exception {
		//BasicConfigurator.configure();
		Configurator.initialize(new DefaultConfiguration());
	}
	
	/**
	 * Premier test de lecture
	 * @throws Exception _
	 */
	@Test
	public void testA()
	throws Exception
	{
		RsfaceReader rr = new RsfaceReader("2017");
		FszNode nd = rr.readRSFACE(
 				 "A91001944791000028022268079168702569000172407397268079168702569U1410031  012507196810601201706012017915300000000000000000000050300000503000000000000000000000000000000000  010                   000161004", 1
				);
		StringBuffer sb = new StringBuffer();
		nd.dump(sb, 0);
		lg.info("nd:"+sb);
	}
	    
}
