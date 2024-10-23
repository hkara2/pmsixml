package fr.gpmsi.pmsixml.nx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Un enregistrement, qui correspond à un enregistrement au format NX.
 * Un enregistrement a une définition, et comprend des champs (nommés), des sous-enregistrements (nommés),
 * des collections (nommées), et des listes d'enregistrements listeEnfants (accessibles par nom).
 */
public class Enregistrement {
	static Logger lg = LogManager.getLogger();
	
	Enregistrement pred, succ;
	DefEnregistrement definition;
	String rub;
	String seq;

	HashMap<String, Champ> champParNom = new HashMap<String, Champ>();
	HashMap<String, List<SousEnregistrement>> sousEnregistrementsParNom = new HashMap<String, List<SousEnregistrement>>();
	/** enregistrements en mode "étend", en fait on ajuste juste à la collection de champs */
	HashMap<String, List<Champ>> collections = new HashMap<String, List<Champ>>();
	HashMap<String, Boolean> collectionsOptNumeroter = new HashMap<String, Boolean>(); //contient Y ou N pour dire si on numerote ou pas la collection
	/** enregistrements reliés */
	HashMap<String, LinkedList<Enregistrement>> listeEnfants = new HashMap<String, LinkedList<Enregistrement>>();
	
	boolean fusionne = false; //si true, a deja ete fusionne, avancer encore sur le precedent jusqu'a trouver celui qui est à l'origine de la fusion
	
	/** constructeur simple */
	public Enregistrement() {}
	
	/**
	 * Rechercher un enregistrement précédent pour type-rub-seq, ou type-rub, ou juste type.
	 * @param typeRubSeq Le "type-rubrique-séquence" à rechercher (ex : "101-01-01")
	 * @return Le premier enregistrement trouvé ou null
	 */
	public Enregistrement findPred(String typeRubSeq) {
		if (typeRubSeq == null || typeRubSeq.length() < 3) return null;
		StringTokenizer stok = new StringTokenizer(typeRubSeq, "-");
		String type = stok.nextToken();
		String rub = null;
		if (stok.hasMoreTokens()) rub = stok.nextToken();
		String seq = null;
		if (stok.hasMoreTokens()) seq = stok.nextToken();
		Enregistrement p = pred;
		while (p != null) {
			if (type.equals(p.definition.type)) {
				if (rub == null) return p;
				if (rub.equals(p.rub)) {
					if (seq == null) return p;
					if (seq.equals(p.seq)) return p;
				}
			}
			p = p.pred;
		}
		//non trouvé, retourner null
		lg.error("Pred non trouve : '"+typeRubSeq+"'");
		return null;
	}

	/**
	 * Ajouter à la chaîne l'enregistrement n2
	 * @param n2 L'enregistrement à ajouter à la chaîne, derrière celui-ci.
	 */
	public void append(Enregistrement n2) {
		if (n2 == null) throw new NullPointerException();
		if (succ != null) { 
			succ.pred = n2;
			n2.succ = succ;
		}
		succ = n2;
		n2.pred = this;
	}
	
	/**
	 * Joindre les champs et les répétitions de champs avec un autre enregistrement e2.
	 * @param e2 L'enregistrement dont on prend les champs et répétitions de champs à ajouter.
	 */
	public void joindreChampsEtReps(Enregistrement e2) {
		for (String key : e2.champParNom.keySet()) {
			Champ c = champParNom.get(key);
			if (c == null) champParNom.put(key, e2.champParNom.get(key));
			else {
				c.join(e2.champParNom.get(key));
			}
		}//for
		//joindre aussi les valeurs des collections
		for (String key : e2.collections.keySet()) {
			List<Champ> e2coll = e2.collections.get(key);
			boolean numeroter = e2.collectionsOptNumeroter.get(key);
			List<Champ> coll = getOrMakeCollection(key, numeroter);
			coll.addAll(e2coll);
		}
	}
	
	/**
	 * Retourner une collection de champs qui a le nom donné.
	 * Si la collection n'existe pas, elle est créée.
	 * @param name Le nom de la collection
	 * @param numeroter Un boolean utiliser si on doit creer la collection, qui dit si on doit ajouter des numeros lorsqu'on emet la collection
	 * @return Une liste de Champ(s). Ne retourne jamais null.
	 */
	public List<Champ> getOrMakeCollection(String name, boolean numeroter) {
		List<Champ> coll = collections.get(name);
		if (coll == null) {
			coll = new ArrayList<Champ>();
			collections.put(name, coll);
			collectionsOptNumeroter.put(name, numeroter);
		}
		return coll;
	}
	
	/**
	 * Retourner une représentation sommaire de cet enregistrment : définition + rubrique + séquence.
	 * @return La représentation de cet enregistrement.
	 */
	@Override
	public String toString() {
		return definition + ", rub '"+rub+"', seq '"+seq+"'";
	}
		
	/**
	 * Retourner le Champ pour le nom donné
	 * @param nom Le nom du champ
	 * @return Le champ
	 */
	public Champ getChamp(String nom) {
		return champParNom.get(nom);
	}
	
	/**
	 * Retourner la liste de sous-enregistrements pour le nom donné
	 * @param nom Le nom de la liste
	 * @return La liste
	 */
	public List<SousEnregistrement> getSousEnregistrement(String nom) {
		return sousEnregistrementsParNom.get(nom);
	}
	
	/**
	 * Retourne l'option "numéroter" pour la collection qui a le nom donné
	 * @param nom Le nom de la collection.
	 * @return True si on numérote cette collection lorsque le XML est généré.
	 */
	public Boolean getOptNumeroter(String nom) {
		return collectionsOptNumeroter.get(nom);
	}
	
	/**
	 * Retourne la liste d'listeEnfants pour le nom donné.
	 * @param nom Le nom de liste
	 * @return La liste
	 */
	public List<Enregistrement> getListeEnfants(String nom) {
		return listeEnfants.get(nom);
	}
}
