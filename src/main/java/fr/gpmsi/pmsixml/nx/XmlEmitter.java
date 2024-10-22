package fr.gpmsi.pmsixml.nx;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Emetteur XML qui envoie le XML pour les données NX.
 */
public class XmlEmitter {
    /**
     * Le logger log4j
     */
	public static Logger lg = LogManager.getLogger(XmlEmitter.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	Document doc;
	DocumentBuilder db;
	Element root;
	
	/**
	 * Constructeur simple
	 * @throws ParserConfigurationException Si le constructeur XML est mal configuré
	 */
	public XmlEmitter()
			throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.newDocument();
		root = doc.createElement("NX"); //a mettre en parametre
		doc.appendChild(root);
	}

	/**
	 * Retourner l'élément racine
	 * @return L'élément racine
	 */
	public Element getRoot() { return root; }
	
	/**
	 * Envoyer l'enregistrement en tant que noeud, 
	 * rattaché au noeud attach_node
	 * @param e L'élément à envoyer
	 * @param attach_node Le noeud auquel rattacher
	 */
	public void emit(Enregistrement e, Node attach_node) {
		//lg.debug("Enregistrement "+e+", nom:"+e.definition.nom);
		Element e_nd = doc.createElement(e.definition.nom);
		attach_node.appendChild(e_nd);
		e_nd.setAttribute("TYP", e.definition.type);
		if (e.rub != null && e.rub.length() > 0) e_nd.setAttribute("RUB", e.rub);
		if (e.seq != null && e.seq.length() > 0)e_nd.setAttribute("SEQ", e.seq);
		//e_nd.setAttribute(null, e.);
		//ajouter les champs
		for (String nom : e.champParNom.keySet()) {
			Champ ch = e.champParNom.get(nom);
			//lg.debug("Creation element '"+ch.definition.nom+"'");
			Element champ_nd = doc.createElement(ch.definition.nom);
			champ_nd.setTextContent(ch.toString());
			e_nd.appendChild(champ_nd);
		}
		//ajouter les sous-enregistrements
		for (String key : e.sousEnregistrementsParNom.keySet()) {
			//lg.debug("Sous enregistrements "+key);
			List<SousEnregistrement> ses = e.sousEnregistrementsParNom.get(key);
			//lg.debug("nb se : "+ses.size());
			for (SousEnregistrement se : ses) {
				Element se_nd = doc.createElement(key);
				e_nd.appendChild(se_nd);
				for (Champ ch : se.champs) {
					Element ch_nd = doc.createElement(ch.definition.nom);
					//lg.debug("champ "+ch.definition.nom+", val='"+ch.toString()+"', traiterCrLf:"+ch.definition.traiterCrLf);
					ch_nd.setTextContent(ch.toString());
					se_nd.appendChild(ch_nd);
				}//for
			}//for
		}//for
		//ajouter les collections
		for (String key : e.collections.keySet()) {
			Element coll_nd = doc.createElement(key);
			e_nd.appendChild(coll_nd);
			List<Champ> coll = e.collections.get(key);
			boolean numeroter = e.collectionsOptNumeroter.get(key);
			int n = 1;
			for (Champ ch : coll) {
				//lg.debug("Coll val "+val.definition.nom+" : '"+val.toString()+"'");
				Element ch_nd = doc.createElement(ch.definition.nom);
				if (numeroter) {
					//ajouter le numéro de chaque élément dans l'attribut N
					ch_nd.setAttribute("N", Integer.toString(n));
					n++;
				}
				ch_nd.setTextContent(ch.toString());
				coll_nd.appendChild(ch_nd);
			}//for
		}//for
		//ajouter les listeEnfants
		for (String key : e.listeEnfants.keySet()) {
			LinkedList<Enregistrement> childList = e.listeEnfants.get(key);
			for (Enregistrement child : childList) {
				emit(child, e_nd);
			}//for
		}//for
	}
	
}
