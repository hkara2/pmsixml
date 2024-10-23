package fr.gpmsi.pmsixml.nx;

import java.util.ArrayList;
import java.util.List;

/**
 * Une définition de sous-enregistrement, c'est à dire une répétition de champs qui se suivent.
 */
public class DefSousEnregistrement {
	int min; //nb minimum de répétitions
	int max; //nb maximum de répétitions (donne la taille)
	String nom;
	String nomLong;
	
	String description;

	ArrayList<DefChamp> champs = new ArrayList<DefChamp>();
	
	/** Constructeur simple */
	public DefSousEnregistrement() {}
	
	/**
	 * Ajouter une définition de sous-enregistrement
	 * @param def La définition
	 */
	public void add(DefChamp def) {
		champs.add(def);
	}
	
	/**
	 * Retourner la liste des définitions de champs
	 * @return La liste des champs
	 */
	public List<DefChamp> getChamps() {
		return champs;
	}

	/**
	 * returner le nombre minumum de répétitions
	 * @return Le nombre
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Définir le nombre minimum de répétitions
	 * @param min Le nombre
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * Définir le nombre minimum de répétitions
	 * @param min Une String qui contient le nombre
	 */
	public void setMin(String min) {
		this.min = Integer.valueOf(min);
	}

	/**
	 * Retourner le nombre maximum de répétitions (cela donne aussi la taille du sous-enregistrement)
	 * @return Le nombre
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Définir le nombre maximum de répétitions
	 * @param max Le nombre
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Définir le nombre maximum de répétitions
	 * @param max Une String avec le nombre
	 */
	public void setMax(String max) {
		this.max = Integer.valueOf(max);
	}

	/**
	 * Retourner le nom de code du sous-enregistrement
	 * @return Le nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Définir le nom de code du sous-enregistrement
	 * @param nom Le nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * Retourner le nom long du sous-enregistrement
	 * @return Le nom long
	 */
	public String getNomLong() {
		return nomLong;
	}

	/**
	 * Définir le nom long du sous-enregistrement
	 * @param nomLong Le nom long
	 */
	public void setNomLong(String nomLong) {
		this.nomLong = nomLong;
	}

	/**
	 * Retourner la description
	 * @return La description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Définir la description
	 * @param description La description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
