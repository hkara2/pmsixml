package fr.gpmsi.pmsixml;

//import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

public class RssWriterTest {

	public static String rss1 = "1106C144 11617091001944701600000000000003133706014215832           4         0701192826112  0512201461050120157191650000000        000401000K624            000                                 E440    K591    E8760   E86     Z740    ";
	public static String rss2 = "1123Z02Z 11617091001944701600000000000003142160014249867           1         3105192924300  1411201485151120146191150000000        000400002N390            000                                 R18     C56     I11     E1188   14112014ZZHJ00701     0 0115112014ZCQK00201 Z   0 01";
	public static String rss3 = "1123Z02Z 11617091001944701600000000000003142160014249867           2         3105192924001081511201461130120156391150000000        000800007Z514    R33     000                                 B962    R18     J690    N10     C774    C786    R590    C56     09012015LMFA00101 J   0 0106012015ZBQK00201 Z   0 0116122014ZBQK00201 Z   0 0118122014ZZQH03301 Z   0 0109012015LMFA00104     0 0118112014ZBQK00201 Z   0 0118122014YYYY60001     0 01";

	public RssWriterTest() {
	}

	void testTxtConv(String rss) 
	throws Exception
	{
		RssReader rssRdr = new RssReader();
		RssWriter rssTxtConv = new RssWriter();
		FszNode nd1 = rssRdr.readOne(rss);
		StringBuffer sb = new StringBuffer();
		rssTxtConv.writeRum((FszGroup)nd1, sb);
		System.out.println("Original:"+rss);
		System.out.println("To text :"+sb);
		System.out.println();
	}
	
	public void run()
			throws Exception 
	{
		testTxtConv(rss1);
		testTxtConv(rss2);
		testTxtConv(rss3);
	}
	
	public static void main(String[] args)
	throws Exception
	{
		//BasicConfigurator.configure();
		Configurator.initialize(new DefaultConfiguration());
		//Logger.getRootLogger().setLevel(Level.INFO);
		RssWriterTest app = new RssWriterTest();
		app.run();
	}

}
