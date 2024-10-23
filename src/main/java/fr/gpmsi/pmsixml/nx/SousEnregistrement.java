package fr.gpmsi.pmsixml.nx;

import java.util.ArrayList;

/**
 * Un sous-enregistrement NX
 */
public class SousEnregistrement {
	DefSousEnregistrement def;
	
	ArrayList<Champ> champs = new ArrayList<Champ>();

	/**
	 * Constructeur simple
	 */
	public SousEnregistrement() {
    }
	
	/**
	 * Ajouter un champ
	 * @param c Le champ à ajouter
	 */
	public void addChamp(Champ c) {
		champs.add(c);
	}
	
	/**
	 * Ajouter un champ en le construisant depuis la ligne à la position
	 *   donnée
	 * @param def La définition du champ
	 * @param line Le numéro de la ligne
	 * @param p La position à partir de laquelle faire l'analyse
	 */
	public void addChamp(DefChamp def, String line, Position p) {
		Champ c = def.makeChamp(line, p);
		if (c != null) addChamp(c);
	}
	
	/**
	 * Tester si le sous-enregistrement est vide.
	 * Il est vide si tous ses champs sont vides.
	 * @return true si sous-enregistrement est vide
	 */
	public boolean isEmpty() {
		for (Champ c : champs) {
			if (!c.isEmpty()) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String name = "";
		if (def != null && def.nom != null) name = def.nom;
		int nbChamps = champs.size();
		return "[Sous-enregistrement "+name+" , "+nbChamps+" champs]";
	}
}
