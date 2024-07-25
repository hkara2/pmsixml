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
	
	public void add(DefChamp def) {
		champs.add(def);
	}
	
	public List<DefChamp> getChamps() {
		return champs;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMin(String min) {
		this.min = Integer.valueOf(min);
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMax(String max) {
		this.max = Integer.valueOf(max);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNomLong() {
		return nomLong;
	}

	public void setNomLong(String nomLong) {
		this.nomLong = nomLong;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
