package fr.gpmsi.pmsixml.tests;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
//import junit.framework.TestCase;
//import org.junit.Test;
import org.junit.jupiter.api.Test;

import fr.gpmsi.pmsixml.FszNode;
import fr.gpmsi.pmsixml.RhsReader;

public class RhsReaderTest
//extends TestCase
{
	static Logger lg = LogManager.getLogger(RhsReaderTest.class);

	@BeforeEach
	protected void setUp() throws Exception {
		//BasicConfigurator.configure();
		Configurator.initialize(new DefaultConfiguration());
	}
	
	@Test
	public void testOne()
	throws Exception
	{
		RhsReader rr = new RhsReader();
		FszNode nd = rr.readOne(
 				 "910019447M093687123016584352                       0404201617042017261119612915801040420167117042017711220171111111611752A        Z5188   G931    F09     4444440500000 F322    G408    F411    R2630   Z741    "			 
				);
		StringBuffer sb = new StringBuffer();
		nd.dump(sb, 0);
		lg.info("nd:"+sb);

	}
	
	@Test
	public void testOneWithTruncated()
	throws Exception
	{
		RhsReader rr = new RhsReader();
		FszNode nd = rr.readOne(
 				 "910019447M093687123016584352                       0404201617042017261119612915801040420167117042017711220171111111611752A        Z5188   G931    F09     4444440500000 F322    G408    F411    R2630   Z741", true
				);
		StringBuffer sb = new StringBuffer();
		nd.dump(sb, 0);
		lg.info("nd:"+sb);

	}
	
	@Test
    public void testOneWithActs()
    throws Exception
    {
        RhsReader rr = new RhsReader();
        FszNode nd = rr.readOne(
                 "910019447M092024   018210230           031115B2 00 21022017060420172302193119115012102201761          0820170111111460059A        Z04880  R33     N40     4114430700600 E105    I10     E440    E559    I258    D530    Z741    ALQ+247         10 01210220170101  PEQ+017         10 01210220170101  ZGQ+137         10 01210220170101  NKR+117         22 01240220170101  NKR+117         22 01220220170101  QZQ+255         10 01210220170101  "
                );
        StringBuffer sb = new StringBuffer();
        nd.dump(sb, 0);
        lg.info("nd:"+sb);

    }
	
	@Test
    public void testOneTruncatedWithActs()
    throws Exception
    {
        RhsReader rr = new RhsReader();
        FszNode nd = rr.readOne(
                 "910019447M092024   018210230           031115B2 00 21022017060420172302193119115012102201761          0820170111111460059A        Z04880  R33     N40     4114430700600 E105    I10     E440    E559    I258    D530    Z741    ALQ+247         10 01210220170101  PEQ+017         10 01210220170101  ZGQ+137         10 01210220170101  NKR+117         22 01240220170101  NKR+117         22 01220220170101  QZQ+255         10 01210220170101",
                 true
                );
        StringBuffer sb = new StringBuffer();
        nd.dump(sb, 0);
        lg.info("nd:"+sb);

    }
    
	public static void main(String[] args) {
		
	}

}
