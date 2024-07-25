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

public class XmlWriter {
	
	Transformer transf;
	
	public XmlWriter()
			throws TransformerConfigurationException 
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transf = transformerFactory.newTransformer();

        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
	}

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
