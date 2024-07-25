package fr.gpmsi.pmsixml.nx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.gpmsi.pmsixml.MetaFileLoader;
import fr.gpmsi.pmsixml.MissingMetafileException;

/**
 * Transformation d'un fichier NX en fichier XML.
 * Attention pour la CCAM il y a besoin de beaucoup de mémoire, utiliser l'option -Xmx4096m pour dire
 * à java qu'il peut utiliser jusqu'à 4 Go de mémoire. Un fichier de 100 MO de CCAM au format NX donnera
 * un fichier de 300 MO au format XML; une fois compressé en ZIP au réglage normal, ce sera un fichier de 10 MO,
 * et si on utilise le format 7z avec le réglage "ultra" on peut même avoir un fichier de 1,5 MO.
 * A noter que si on compresse le fichier initial au format NX en utilisant 7z avec le réglage "ultra", on
 * obtiendra aussi un fichier de 1,5 MO !
 */
public class Nx2Xml {
	public static Logger lg = LogManager.getLogger();
	private static final boolean TRACE_RECHERCHE = false;
	private static final boolean TRACE_LECT_LIGNES = false;
	
	public static int lineNr; //variable "globale", utilisée pour indiquer les erreurs dans les autres classes -> ne pas lancer de multiples instances concurrentes !
	
	File nxFile;
	File xmlFile;
	
	/** historique de tous les enregistrements lus, liés entre eux. Sert à naviguer vers les précédents */
	Enregistrement first, last;
	
	Enregistrement enregistrement000;
	
	/** Les enregistrements définitifs */
	ArrayList<Enregistrement> enregistrements = new ArrayList<Enregistrement>();

	Map<String, String> champs000 = null;

	NxMeta m = null;
	
	String metaFileName;
	String metaFilePath; //si non null, forcera l'utilisation de ce fichier
	
	public void init(String nxFilePath, String xmlFilePath) {
		nxFile = new File(nxFilePath);
		xmlFile = new File(xmlFilePath);
	}
	

	/**
	 * Retourner l'enregistrement precedent.
	 * Utile lorsqu'il faut fusionner les enregistrements avec SEQ qui se suivent.
	 * @return l'enregistrement précédent
	 */
	public Enregistrement getPred() {
		Enregistrement e = last;
		if (e == null) return null; //ne devrait pas se produire
		e = e.pred; //naviguer sur l'enregistrement precedent
		while (e != null && e.fusionne) e = e.pred; //reculer jusqu'au premier qui est non-fusionne
		return e;
	}
	
	/**
	 * Rechercher enregistrement passé.
	 * La spécification donne ce qu'il y a a chercher
	 * @param spec Soit type, soit type-rub, soit type-rub-seq. Ex : 101-01-01
	 * @return l'enregistrement passé
	 */
	public Enregistrement findPast(String spec) {
		String type = spec.substring(0, 3);
		Integer rub = null;
		//101-01-01
		//0123456789
		if (spec.length() >= 6) rub = Integer.valueOf(spec.substring(4, 6));
		Integer seq = null;
		if (spec.length() == 9) seq = Integer.valueOf(spec.substring(7, 9));
		if (TRACE_RECHERCHE) lg.debug("Recherche type '"+type+"' rub '"+rub+"' seq '"+seq+"'");
		Enregistrement e = last;
		while (e != null) {
			if (TRACE_RECHERCHE) lg.debug("Examen de "+e);
			if (type.equals(e.definition.type)) {
				//le type est OK
				if (rub != null) { //est-ce qu'on attend une rubrique spécifique ?
					if (rub.equals(e.definition.getRubriqueMax())) {
						if (seq != null) { //est-ce qu'on attend une séquence spécifique ?
							if (seq.equals(e.definition.getSequenceMax())) {
								//on a type, rub et seq qui concordent, c'est OK.
								if (TRACE_RECHERCHE) lg.debug("Trouve "+e);
								return e;
							}
						}
						else {
  						    //on a type, rub qui concordent, c'est OK.
							if (TRACE_RECHERCHE) lg.debug("Trouve "+e);
							return e;
						}
					}
				}
				else {
					//on a type qui concorde, c'est OK.
					if (TRACE_RECHERCHE) lg.debug("Trouve "+e);
					return e;
				}
			}
			//pas de concordance selon ce qui est désiré, passer au prédécesseur
			e = e.pred; //retourner en arrière et prendre le prochain
		}//while
		//rien trouvé, on ramène null.
		return null;
	}
	
	/**
	 * Relier l'enregistrement selon son type, ou sinon l'enregistrer dans les enregistrements normaux.
	 * @param e L'enregistrement à éventuellement relier
	 */
	public void relierEnregistrement(Enregistrement e) {
		DefEnregistrement def = e.definition;
		if (def.etend != null) {
			//cet enregistrement étend un autre enregistrement, c'est à dire que les infos s'ajoutent directement
			//à l'autre enregistrement
			Enregistrement ee = findPast(def.etend);
			if (ee == null) {
				lg.error("L'enregistrement étend un enregistrement "+def.etend+" mais il n'a pas été trouvé");
			}
			else {
				//ee.champParNom.putAll(e.champParNom);
				ee.joindreChampsEtReps(e);
				//normalement si on a "étend", il ne vaut mieux pas qu'il y ait d'listeEnfants.
				//cependant, s'il y en a, on les ajoute à la liste des listeEnfants de cet enregistrement
				for (String key : e.listeEnfants.keySet()) {
					LinkedList<Enregistrement> listeEnr = e.listeEnfants.get(key);
					if (listeEnr != null) {
						if (ee.listeEnfants.containsKey(key)) {
							ee.listeEnfants.get(key).addAll(listeEnr); //ajouter à la liste déjà existante
						}
						else ee.listeEnfants.put(key, listeEnr);
					}
				}//for
			}							
		}
		else if (def.lien != null) {
			boolean relier = traiterFusionSeq(e);
			if (relier) {
				//cet enregistrement a un lien de parenté avec un autre enregistrement
				Enregistrement ep = findPast(def.lien);
				if (ep == null) {
					lg.error("L'enregistrement est lie a un enregistrement parent "+def.etend+" mais il n'a pas été trouvé");
				}
				else {
					LinkedList<Enregistrement> listeEnf = ep.listeEnfants.get(def.nom);
					if (listeEnf == null) {
						listeEnf = new LinkedList<Enregistrement>();
						ep.listeEnfants.put(def.nom, listeEnf);
					}
					listeEnf.add(e);
				}							
			}
		}
		else {
			traiterFusionSeq(e);
			if (!e.fusionne) enregistrements.add(e);
		}
	}
	
	/**
	 * Taiter les fusions sur séquence si cela se présente.
	 * Fonctionne sur champs normaux et champs qui ont "etend", mais pas sur les champs avec "lien"
	 * @param e l'enregistrement à traiter
	 * @return true si il faut relier cet enregistrement au précédent, false sinon (si cet enregistrement a été 
	 * fusionné avec le précédent, il ne faut pas le relier à la chaîne).
	 */
	public boolean traiterFusionSeq(Enregistrement e) {
		DefEnregistrement def = e.definition;
		boolean relier = true;
		//if (def.fusionneSur != null) lg.debug("Fusionne sur : "+def.fusionneSur);
		//cas particulier de l'enregistrement qui continue un enregistrement precedent qui a donc déjà été relié à l'autre enregistrement.
		if ("SEQ".equals(def.fusionneSur)) {
			Enregistrement ePred = getPred();
			//lg.debug("Pred : "+ePred);
			if (e.definition.nom.equals(ePred.definition.nom)) {
				//verifier que SEQ est toujours precedent au SEQ étendu
				if (e.rub.equals(ePred.rub) && Integer.valueOf(e.seq) > Integer.valueOf(ePred.seq)) {
					//String texte_note = e.champParNom.get("TEXTE_NOTE").valeurAlpha;
					//String texte_notePred = ePred.champParNom.get("TEXTE_NOTE").valeurAlpha;
					//lg.debug("Jonction des enregistrements. '"+ePred+"'("+texte_notePred+") et '"+e+"'("+texte_note+")");
					//lg.debug("Jonction des enregistrements. '"+ePred+"' et '"+e+"'");
					ePred.joindreChampsEtReps(e);
					relier = false; //on a fusionne, désactiver la liaison.
					e.fusionne = true; //e a été fusionné avec le précédent, marquer cela pour que le suivant soit aussi fusionné avec ce précédent
				}
			}
		}
		return relier;
	}
	

	/**
	 * Retourner le chemin où on doit rechercher les métadonnées supplémentaires
	 * @return Le chemin
	 */
	public String getMetaFilePath() {
		return metaFilePath;
	}


	/**
	 * Définir le chemin où sont stockées les métadonnées supplémentaires (par défaut : null)
	 * @param metaFilePath Le chemin
	 */
	public void setMetaFilePath(String metaFilePath) {
		this.metaFilePath = metaFilePath;
	}

	/**
	 * Enlever les sous-enregistrements vides qui sont en fin de liste
	 * @param e L'enregistrement duquel on veut enlever les sous-enregistrements vides
	 */
	public void enleverVides(Enregistrement e) {
		//enlever sous-enregistrements vides
		for (String key : e.sousEnregistrementsParNom.keySet()) {
			List<SousEnregistrement> seLst = e.sousEnregistrementsParNom.get(key);
			int seLstSize = seLst.size();
			for (int i = seLstSize - 1; i >= 0; i--) {
				SousEnregistrement se = seLst.get(i);
				if (se.isEmpty()) seLst.remove(i);
				else break;
			}//for
		}
		ArrayList<String> keysToRemove = new ArrayList<String>();
		//enlever champs vides des collections
		for (String key : e.collections.keySet()) {
			List<Champ> cLst = e.collections.get(key);
			int cLstSize = cLst.size();
			for (int i = cLstSize - 1; i >= 0; i--) {
				//lg.debug("i="+i);
				Champ c = cLst.get(i);
				//lg.debug("champ "+c.definition.nom+"="+c+", isEmpty : "+c.isEmpty());
				if (c.isEmpty()) cLst.remove(i);
				else break;
			}
			if (cLst.isEmpty()) keysToRemove.add(key); //si la collection est devenue vide, l'enlever
		}//for
		for (String k : keysToRemove) e.collections.remove(k);
		//vider les listeEnfants
		for (String key : e.listeEnfants.keySet()) {
			LinkedList<Enregistrement> eLst = e.listeEnfants.get(key);
			for (Enregistrement ee : eLst) enleverVides(ee);
		}
	}
	
	/**
	 * Exécuter 
	 * @throws MissingMetafileException _
	 * @throws ParserConfigurationException _
	 * @throws SAXException _
	 * @throws IOException _
	 * @throws NxMetaParseException _
	 * @throws NxParseException _
	 * @throws TransformerException _
	 */
	public void run()
			throws MissingMetafileException, ParserConfigurationException, SAXException, IOException, NxMetaParseException, NxParseException, TransformerException
	{
		//chargement du fichier au format ISO-8859-1 (obligatoire pour la norme NX)
		try(BufferedReader br = new BufferedReader(new FileReader(nxFile, Charset.forName("ISO-8859-1")))) {
			String line = br.readLine();
			lineNr = 1;
			if (TRACE_LECT_LIGNES) lg.debug(">"+lineNr+">"+line+"<<");
			if (line != null) {
				//traitement particulier de la ligne 1 qui donne le nom de la métadonnée, ce qui permet de charger
				//la bonne métadonnée à partir du fichier donné
				champs000 = NxMeta.enregistrement000(line);
				//IDENTIFICATION_FICHIER" suivi du champ "NUMERO_VERSION
				metaFileName = champs000.get("IDENTIFICATION_FICHIER") + champs000.get("NUMERO_VERSION") + ".xml";
				//charger metadonnee
				MetaFileLoader mfl = new MetaFileLoader();
				DocumentBuilderFactory fac = DocumentBuilderFactory.newDefaultInstance();
				DocumentBuilder builder = fac.newDocumentBuilder();
				InputSource isrc;
				if (metaFilePath != null) {
					//si un fichier est forcé cela a priorité pour le chargement des métadonnées
					isrc = new InputSource(new FileInputStream(new File(metaFilePath)));
				}
				else {
					//trouver le bon fichier des métadonnées dans les resources
					InputStream is = mfl.getInputStream("nx/" + metaFileName);
					isrc = new InputSource(is);
				}
				Document doc = builder.parse(isrc);
				m = new NxMeta();
				m.load(doc);
				lg.info("Metadonnees chargees.");
			}
			while (line != null) {
				//System.out.print("\rLigne "+lineNr+"      ");
				if (line.length() != 128) {
					throw new NxParseException("ligne incompatible avec le format nx en ligne " + lineNr + ", longueur = "+line.length()+", >"+line+"<");
				}
				String type = line.substring(0, 3);
				String rub = line.substring(3, 5);
				String seq = line.substring(5, 7);
				if (TRACE_LECT_LIGNES) lg.debug("type:"+type+",rub:"+rub+",seq:"+seq);
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
				//faire un enregistrement et le relier dans l'historique
				Enregistrement enr = de.makeEnregistrement(line);
				if (last == null) {
					first = enr;
					last = enr;
					last.pred = null;
					last.succ = null;
				}
				else {
					last.append(enr);
					last = enr;
				}
				relierEnregistrement(enr);
				line = br.readLine();
				if (TRACE_LECT_LIGNES) lg.debug(">"+lineNr+">"+line+"<<");
				lineNr++;
			}//while
			XmlEmitter xe = new XmlEmitter();
			Element root = xe.getRoot();
			/*
			 * pour debogage
			Enregistrement en = first;
			while (en != null) {
				lg.debug("Emission de "+en.definition.nom);
				xe.emit(en, root);
				en = en.succ;
			}
			*/
			for (Enregistrement e : enregistrements) {
				enleverVides(e);
				xe.emit(e, root);
			}
			//int nrOfChildren = xe.doc.getChildNodes().getLength();
			//lg.debug("Nr of children : "+nrOfChildren);
			XmlWriter xw = new XmlWriter();
			xw.writeToFile(xe.doc, xmlFile);
			br.close(); //not necessary, but no harm			
		}
	}
	
	/**
	 * Point de lancement de la conversion nx vers xml.
	 * Il y a deux arguments obligatoires : le chemin vers le fichier NX à convertir, et le chemin vers le fichier XML qui résulte de la conversion.
	 * Si un troisième argument est fourni, ce doit être un fichier de métadonnées qui fait correspondre les champs NX avec une structure XML à générer.
	 * Normalement ce fichier est trouvé automatiquement à l'aide des informations de l'enregistrement 000 et chargé dans les ressources.
	 * @param args Les arguments
	 * @throws Exception Si erreur
	 */
	public static void main(String[] args) 
	throws Exception
	{
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.

		Nx2Xml app = new Nx2Xml();
		if (args.length < 2) throw new Exception(args.length + " argument(s) donnés, 2 ou 3 attendus");
		//app.init("C:\\Users\\Harry\\Downloads\\classement\\PMSI\\Ameli\\NX\\CCAM\\CACTOT07500\\CACTOT07500", "files-for-tests\\tmp-out\\CACTOT07500.XML");
		//app.init("files-for-tests\\nx\\CCAM2.txt", "files-for-tests\\tmp-out\\CCAM2.xml");
		//app.init("files-for-tests\\nx\\CACTOT07500_reduit-020.txt", "files-for-tests\\tmp-out\\CACTOT07500_reduit-020.xml");
		app.init(args[0], args[1]);
		if (args.length >= 3) app.setMetaFilePath(args[2]);
		app.run();
		lg.info("Traitement termine.");
	}

}
