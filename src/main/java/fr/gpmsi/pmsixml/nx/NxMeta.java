package fr.gpmsi.pmsixml.nx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.gpmsi.pmsixml.xml.NodeUtils;

/**
 * Objet qui contient les métadonnées NX lues pour une version de format
 * donnée (pas testé pour les anciens formats d'avant 2020).
 */
public class NxMeta {
	
    /**
     * Logger à utiliser pour les traces
     */
	public static Logger lg = LogManager.getLogger();
	static final boolean TRACE_META_LOADING = false;

	//DefChamp champ;
	HashMap<String, List<DefEnregistrement>> defEnregistrementsByType = new HashMap<String, List<DefEnregistrement>>();
	
	/**
	 * Retourner les définitions d'enregistrement pour le type nommé
	 * @param type Le type
	 * @return La liste des définitions
	 */
	public List<DefEnregistrement> getDefEnregistrements(String type) {
		return defEnregistrementsByType.get(type);
	}
	
	/**
	 * Rechercher une définition d'enregistrement qui accepte le type, la rubrique et la séquence qui
	 * sont passées en argument.
	 * @param type Le type
	 * @param rub La rubrique
	 * @param seq La séquence
	 * @return Une définition compatible ou null si non trouvé
	 */
	public DefEnregistrement findDefEnregistrement(String type, String rub, String seq) {
		List<DefEnregistrement> lst = getDefEnregistrements(type);
		if (lst == null) return null;
		for (DefEnregistrement de : lst) {
			if (de.isRubriqueValid(rub) && de.isSequenceValid(seq)) return de;
		}
		//pas trouvé, retourner null
		return null;
	}
		
	/**
	 * Charger le document XML de définition et en lire les métadonnées.
	 * @param def Le document
	 * @throws NxMetaParseException Si il y a eu une erreur d'analyse
	 */
	public void load(Document def)
			throws NxMetaParseException 
	{
		Element root = def.getDocumentElement();
		lg.info("Root : "+root);
		NodeList children = root.getChildNodes();
		int childrenCount = children.getLength();
		for (int i = 0; i < childrenCount; i++) {
			Node child = children.item(i);
			if (TRACE_META_LOADING) lg.info("Child: "+child);
			if (child.getNodeName() == "ENR") {
				DefEnregistrement de = makeDefEnregistrement(child);
				List<DefEnregistrement> enregistrements = getDefEnregistrements(de.type);
				if (enregistrements == null) enregistrements = new ArrayList<DefEnregistrement>();
				enregistrements.add(de);
				defEnregistrementsByType.put(de.type, enregistrements);
			}
		}
	}
	
	/**
	 * Créer un enregistrement de définition de répétition à partir du noeud donné.
	 * @param nd Le noeud
	 * @return L'enregistrement de définition de répétition
 	 * @throws NxMetaParseException Si erreur d'analyse
	 */
	public DefRepetition makeRepetition(Node nd)
			throws NxMetaParseException
	{
		NamedNodeMap attributes = nd.getAttributes();
		DefRepetition rep = new DefRepetition();
		String _min = NodeUtils.getAttribute(attributes, "MIN");
		rep.setMin(_min);
		String _max = NodeUtils.getAttribute(attributes, "MAX");
		rep.setMax(_max);
		rep.nom = NodeUtils.getAttribute(attributes, "NOM");
		rep.nomLong = NodeUtils.getAttribute(attributes, "NOMLONG");
		rep.description = NodeUtils.getAttribute(attributes, "DESCRIPTION");
		String numeroter = NodeUtils.getAttribute(attributes, "NUMEROTER");
		rep.numeroter = (numeroter != null && numeroter.toUpperCase().equals("Y"));
		NodeList children = nd.getChildNodes();
		int childrenCount = children.getLength();
		int eltCount = 0;
		for (int i = 0; i < childrenCount; i++) {
			if (eltCount > 1) {
				throw new NxMetaParseException("On ne peut pas mettre plus d'un element dans REPETITION (noeud "+nd.getNodeName()+", nom "+rep.nom+", nomlong"+rep.nomLong+")");
			}
			Node child = children.item(i);
			String nom = child.getNodeName();
			if (nom == "#text") ; //ignorer texte
			else if (nom == "CHAMP") {
				DefChamp champ = makeDefChamp(child);
				//lg.debug("champ a repeter : "+champ);
				rep.setChampARepeter(champ);
				eltCount++;
			}
			else {
				throw new NxMetaParseException("On ne peut mettre qu'un CHAMP dans REPETITION (noeud "+nd+")");
			}
		}
		return rep;
	}
	
	/**
	 * Créer une définition de sous-enregistrement à partir du noeud donné.
	 * @param nd Le noeud DOM
	 * @return L'objet de définition de sous-enregistrement
	 * @throws NxMetaParseException Si erreur d'analyse
	 */
	public DefSousEnregistrement makeSousEnregistrement(Node nd)
			throws NxMetaParseException
	{
		NamedNodeMap attributes = nd.getAttributes();
		DefSousEnregistrement dse = new DefSousEnregistrement();
		String _min = NodeUtils.getAttribute(attributes, "MIN");
		dse.setMin(_min);
		String _max = NodeUtils.getAttribute(attributes, "MAX");
		dse.setMax(_max);
		if (TRACE_META_LOADING) lg.debug("dse max : "+_max);
		dse.nom = NodeUtils.getAttribute(attributes, "NOM");
		dse.nomLong = NodeUtils.getAttribute(attributes, "NOMLONG");
		dse.description = NodeUtils.getAttribute(attributes, "DESCRIPTION");
		NodeList children = nd.getChildNodes();
		int childrenCount = children.getLength();
		for (int i = 0; i < childrenCount; i++) {
			Node child = children.item(i);
			String nom = child.getNodeName();
			if (nom == "CHAMP") {
				DefChamp dc = makeDefChamp(child);
				dse.add(dc);				
			}
			else if (nom == "#text") ; //ignorer les noeuds #text
			else if (nom == "#comment") ; //ignorer les noeuds #comment
			else {
				throw new NxMetaParseException("On ne peut mettre que des CHAMP dans SOUS-ENR ("+makeErrContext(child, dse.nom, dse.nomLong)+")");
			}
		}
		return dse;
	}
	
	/**
	 * Créer une définition de champ à partir du noeud donné
	 * @param nd Le noeud DOM
	 * @return La définition de champ créée
	 */
	public DefChamp makeDefChamp(Node nd) {
		NamedNodeMap attributes = nd.getAttributes();
		DefChamp dc = new DefChamp();
		//<CHAMP NOM="" NOMLONG="Place dans l'arborescence" TYPE="N" LNG="12" ECH="6" DESCRIPTION="" />
		dc.nom = NodeUtils.getAttribute(attributes, "NOM");
		dc.nomLong = NodeUtils.getAttribute(attributes, "NOMLONG");
		String type = NodeUtils.getAttribute(attributes, "TYPE");
		if (type == null || type.length() == 0) type = "A";
		dc.type = type.charAt(0);
		dc.description = NodeUtils.getAttribute(attributes, "DESCRIPTION");
		dc.setLng(NodeUtils.getAttribute(attributes, "LNG"));
		dc.setEch(NodeUtils.getAttribute(attributes, "ECH"));
		String videAtt = NodeUtils.getAttribute(attributes, "VIDE"); 
		if (videAtt != null) dc.setVide(videAtt);
		String traiterCrLfAtt = NodeUtils.getAttribute(attributes, "TRAITER-CRLF");
		dc.traiterCrLf = (traiterCrLfAtt != null && traiterCrLfAtt.toUpperCase().equals("Y"));
		//lg.debug("defChamp "+dc.nom+", traiterCrLf="+dc.traiterCrLf);
		return dc;
	}
	
	/**
	 * Créer une définition d'enregistrement à partir du noeud donné
	 * @param nd Le noeud DOM
	 * @return La définition d'enregistrement
	 * @throws NxMetaParseException Si il y a eu un problème pendant l'analyse
	 */
	public DefEnregistrement makeDefEnregistrement(Node nd)
			throws NxMetaParseException 
	{
		NamedNodeMap attributes = nd.getAttributes();
		DefEnregistrement de = new DefEnregistrement();
		de.nom = NodeUtils.getAttribute(attributes, "NOM");
		de.nomLong = NodeUtils.getAttribute(attributes, "NOMLONG");
		de.type = NodeUtils.getAttribute(attributes, "TYP");
		
		String rubrique = NodeUtils.getAttribute(attributes, "RUB");
		if (rubrique != null) de.setRubrique(rubrique);
		
		String rubriqueMin = NodeUtils.getAttribute(attributes, "RUBMIN");
		if (rubriqueMin != null) de.setRubriqueMin(rubriqueMin);
		
		String rubriqueMax = NodeUtils.getAttribute(attributes, "RUBMAX");
		if (rubriqueMax != null) de.setRubriqueMax(rubriqueMax);
		
		String sequence = NodeUtils.getAttribute(attributes, "SEQ");
		if (sequence != null) de.setSequence(sequence);
		
		String sequenceMin = NodeUtils.getAttribute(attributes, "SEQMIN");
		if (sequenceMin != null) de.setSequenceMin(sequenceMin);
		
		String sequenceMax = NodeUtils.getAttribute(attributes, "SEQMAX");
		if (sequenceMax != null) de.setSequenceMax(sequenceMax);
		
		de.complete  = NodeUtils.getAttribute(attributes, "COMPLETE");
		de.etend   = NodeUtils.getAttribute(attributes, "ETEND");
		de.lien   = NodeUtils.getAttribute(attributes, "LIEN");
		de.fusionneSur   = NodeUtils.getAttribute(attributes, "FUSIONNE-SUR");
		if (de.fusionneSur != null) de.fusionneSur = de.fusionneSur.toUpperCase();//forcer en majuscules pour plus de fiabilite
		
		if (TRACE_META_LOADING) lg.info("Type: "+de.type+", rub:"+de.getRubriqueMax()+", Nom:"+de.nom+", Nom long:"+de.nomLong+(de.fusionneSur == null ? "" : ", fusionne-sur:"+de.fusionneSur));
		
		NodeList children = nd.getChildNodes();
		int childrenCount = children.getLength();
		for (int i = 0; i < childrenCount; i++) {
			Node child = children.item(i);
			String childName = child.getNodeName();
			if (childName == "DESCRIPTION") {
				de.description = child.getTextContent();
			}
			else if (childName == "REPETITION") {
				DefRepetition rep = makeRepetition(child);
				de.addEnfant(rep);
			}
			else if (childName == "SOUS-ENR") {
				DefSousEnregistrement se = makeSousEnregistrement(child);
				de.addEnfant(se);
			}
			else if (childName == "CHAMP") {
				DefChamp dc = makeDefChamp(child);
				de.addEnfant(dc);
			}
			else if (childName == "#text") {
				//ignorer
			}
			else if (childName == "#comment") {
				//ignorer
			}
			else {
				lg.error("noeud enfant non reconnu " + childName);
			}			
		}
		return de;
	}
	
	/**
	 * Lecture de l'enregistrement 000.
	 * Cas particulier : lire la première ligne (enregistrement 000) et en extraire les principaux champs.
	 * Cela permet de charger ensuite le bon fichier de métadonnées. Le nom du fichier de métadonnées est
	 * le champ "IDENTIFICATION_FICHIER" suivi du champ "NUMERO_VERSION" suivi de .xml. Voici les principales
	 * métadonnées par exemple pour le document 2018-03-bases-normes-echange-t2a-ccam-lpp.pdf :
	 * <table border="1">
	 * <tr><td>Domaine concerné</td><td>Nom du fichier de métadonnées</td></tr>
	 * <tr><td>LPP</td>      <td>TIP1701.xml</td></tr>
	 * <tr><td>Biologie</td> <td>BIO0001.xml</td></tr>
	 * <tr><td>Pharmacie</td><td>PHA1801.xml</td></tr>
	 * <tr><td>CCAM</td>     <td>CAM1401.xml</td></tr>
	 * <caption>Exemple métadonnées</caption>
	 * </table>
	 * 
	 * @param line La ligne à utiliser pour lire l'enregistrement
	 * @return Une Map avec quelques enregistrements lus depuis l'enregistrement 000. Les enregistrements qui
	 *   sont toujours lus sont "PROGRAMME_EMETTEUR" (pos 20), "IDENTIFICATION_FICHIER" (pos 50), "NUMERO_VERSION" (pos 90)
     * @throws IllegalArgumentException Si la ligne est incompatible avec le format NX
	 */
	public static Map<String, String> enregistrement000(String line)
	throws IllegalArgumentException
	{
		/* Exemple d'enregistrement 000 (ici pour le LPP) :
		 *  
		 * Information                  Type Debut Lng.   Commentaire
		 * Type d’enregistrement        N        1    3   Valeur ‘000’
		 * Type d’émetteur              A        4    2   Non contrôlé
		 * Numéro d’émetteur            N        6   14   Valeur ‘0000000100000’
		 * Programme émetteur           A       20    6   Valeur ‘CODTIP’
		 * Type de destinataire         A       26    2   Valeur ‘CT’
		 * Numéro de destinataire       N       28   14   Valeur ‘00000001000000’
		 * Programme destinataire       A       42    6   Valeur ‘NAX210’
		 * Application - type d’échange A       48    2   Valeur ‘NA’
		 * Identification du fichier    A       50    3   Valeur ‘TIP’
		 * Date de création du fichier  N       53    6   Sous la forme AAMMJJ
		 * Informations NOEMIE          A       59   24   Inutilisé
		 * Numéro chronologie           N       83    5   Valeur > 00000
		 * Type de fichier              N       88    1   Valeur ‘0’ = mise à jour, ‘1’ = fichier en annule et remplace
		 * Unité monétaire              A       89    1   Valeur ‘F’ = Franc, ‘E’ = Euro
		 * Numéro de version de norme   N       90    4   Version du format de données généré : 1701
		 * Zone inutilisée              A       94   35   Initialisé à blanc
		 * (1 er caractère après la fin)       129
		 * 
		 * Ici les indices de début sont ceux que l'on peut voir dans un éditeur de texte, c'est à dire
		 * que le 1er caractère est dans la première colonne qui porte donc le numéro 1.
		 * 
		 */
		//commencer par quelques vérifications élémentaires
		if (line == null || line.length() != 128) {
			throw new IllegalArgumentException("L'argument line ne contient pas un enregistrement NX");
		}
		String typeDEnregistrement = line.substring(0, 0+3);
		if (!typeDEnregistrement.equals("000")) {
			throw new IllegalArgumentException("L'argument line ne contient pas un enregistrement de type 000");
		}
		//remplir les valeurs. Ici on met 1 de moins que dans le tableau puisque le 1er caractère a l'indice 0 en java.
		HashMap<String, String> m = new HashMap<String, String>();
		m.put("PROGRAMME_EMETTEUR", line.substring(19, 19+6));
		m.put("IDENTIFICATION_FICHIER", line.substring(49, 49+3));
		m.put("NUMERO_VERSION", line.substring(89, 89+4));
		//dans l'avenir, je rajouterai peut-être la lecture du reste des champs
		return m;
	}
	
	/**
	 * Créer un contexte d'erreur pour donner plus d'informations sur la ligne courante, le noeud DOM, le
	 * nom de l'élément, le nom long de l'élément.
	 * @param nd Noeud DOM
	 * @param nom Nom de l'élément (sert à référencer l'élément pour l'API)
	 * @param nomLong Nom long de l'élément (sert surtout à la lecture de la doc)
	 * @return
	 */
	String makeErrContext(Node nd, String nom, String nomLong) {
		return "noeud:"+nd.getNodeName()+",nom:"+nom+",nomLong:"+nomLong+",ligne:"+Nx2Xml.lineNr;
	}
}
