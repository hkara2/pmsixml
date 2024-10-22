package fr.gpmsi.pmsixml.nx;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Le Writer qui va écrire en XML dans le fichier de sortie.
 */
public class XmlWriter {
	
	Transformer transf;
	
	/**
	 * Constructeur simple
	 * @throws TransformerConfigurationException Si erreur de configuration du transformer XML
	 */
	public XmlWriter()
			throws TransformerConfigurationException 
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transf = transformerFactory.newTransformer();

        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
	}

	/**
	 * Ecrire dans le fichier
	 * @param doc Le document XML à écrire
	 * @param myFile Le fichier vers lequel écrire
	 * @throws TransformerException Si il y a eu une erreur lors de la transformation
	 */
	void writeToFile(Document doc, File myFile)
			throws TransformerException
	{
        DOMSource source = new DOMSource(doc);
        //myFile = new File("src/main/resources/users.xml");
        //StreamResult console = new StreamResult(System.out);
        StreamResult fileStreamResult = new StreamResult(myFile);
        //transf.transform(source, console);
        transf.transform(source, fileStreamResult);
	}
}
