package fr.gpmsi.pmsixml.nx;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.gpmsi.pmsixml.MetaFileLoader;
import fr.gpmsi.pmsixml.MissingMetafileException;

/**
 * Prendre un fichier Nx et émettre les marques pour chaque début de champ, à l'aide des
 * numéros de type, rubrique et séquence
 */
public class NxTextMark {
	
	static Logger lg = LogManager.getLogger();
	int lineNr;
	File nxFile;
	File outputFile;
	
	void emitWithMarks(String line, DefEnregistrement md, Writer wr) throws IOException {
		wr.write(line); wr.write("\r\n");
		//emettre les marques
		md.emettreMarques(wr); wr.write("\r\n");
	}
	
	public void run()
			throws MissingMetafileException, ParserConfigurationException, SAXException, IOException, NxMetaParseException, NxParseException, TransformerException
	{
		//charger metadonnee fixe pour l'instant (mise au point)
		MetaFileLoader mfl = new MetaFileLoader();
		InputStream is = mfl.getInputStream("nx/ccam/CAM1401.xml");
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fac.newDocumentBuilder();
		InputSource isrc = new InputSource(is);
		Document doc = builder.parse(isrc);
		NxMeta m = new NxMeta();
		m.load(doc);
		lg.info("Metadonnees chargees.");
		try(BufferedReader br = new BufferedReader(new FileReader(nxFile))) {
			//faire le flux de sortie
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
				String line = br.readLine();
				lineNr = 1;
				lg.debug(">"+lineNr+">"+line+"<<");
				while (line != null) {
					if (line.length() != 128) {
						throw new NxParseException("ligne incompatible avec le format nx en ligne " + lineNr);
					}
					String type = line.substring(0, 3);
					String rub = line.substring(3, 5);
					String seq = line.substring(5, 7);
					lg.debug("type:"+type+",rub:"+rub+",seq:"+seq);
					DefEnregistrement de;
					if (type.equals("000") || type.equals("999")) {
						de = m.findDefEnregistrement(type, "0", "0"); //ces enregistrements spéciaux n'ont ni rubrique ni séquence.
					}
					else {
						de = m.findDefEnregistrement(type, rub, seq);
					}
					if (de == null) {
						throw new NxParseException("Pas de metadonnees pour type:"+type+",rub:"+rub+",seq:"+seq+" en ligne "+lineNr);
					}
					//envoyer la ligne, avec les marques en dessous
					//lg.debug(">"+lineNr+">"+line+"<<");
					emitWithMarks(line, de, bw);
					lineNr++;					
					line = br.readLine();
				}//while
				bw.close(); //not necessary, but no harm	
			}//BufferedReader
			br.close(); //not necessary, but no harm
		}//BufferedWriter
	}
	
	public void init(File inputFile, File outputFile) {
		this.nxFile = inputFile;
		this.outputFile = outputFile;
	}
	
	public static void main(String[] args) 
	throws Exception
	{
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.

		NxTextMark app = new NxTextMark();
		//app.init("C:\\Users\\Harry\\Downloads\\classement\\PMSI\\Ameli\\NX\\CCAM\\CACTOT07500\\CACTOT07500", "files-for-tests\\tmp-out\\CACTOT07500.XML");
		app.init(new File("files-for-tests\\nx\\CCAM1.txt"), new File("files-for-tests\\tmp-out\\CCAM1_with_marks.txt"));
		app.run();
	}

}
