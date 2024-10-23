package fr.gpmsi.pmsixml.nx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DefRepetition concerne une simple répétition d'un champ, un certain nombre de fois.
 * Chacune de ces répétitions peut être ajoutée à une collection, le "nom" de la
 * répétition désigne la collection à laquelle ajouter chacune des répétitions.
 * Si la répétition fait partie d'un enregistrement qui a l'attribut "ETEND", on
 * ajoutera à la collection de l'enregistrement qui est étendu.
 */
public class DefRepetition
extends DefChamp
{
	static final Logger lg = LogManager.getLogger();
	int _min;
	int _max;
	DefChamp champARepeter;
	boolean numeroter = false; //si true, on numerote les elements
	
	/** Constructeur simple */
	public DefRepetition() {}
	
	/**
	 * Retourner le numéro minimum
	 * @return Le numéro minimum
	 */
	public int getMin() {
		return _min;
	}
	
	/**
	 * Définir le numéro minimum
	 * @param _min Le numéro minimum
	 */
	public void setMin(int _min) {
		this._min = _min;
	}
	
	/**
	 * Définir le numéro minimum
	 * @param _min Une String qui contient le numéro minimum
	 */
	public void setMin(String _min) {
		if (_min == null) {
			this._min = 0;
		}
		else {
			try {
				this._min = Integer.valueOf(_min);				
			}
			catch (NumberFormatException ex) {
				lg.error("Erreur, 'min' pas un nombre : " + _min);
				this._min = 0;
			}
		}
	}
	
	/**
	 * Retourner le numéro maximum
	 * @return Le numéro maximum
	 */
	public int getMax() {
		return _max;
	}
	
	/**
	 * Définir le numéro maximum
	 * @param _max Le numéro maximum
	 */ 
	public void setMax(int _max) {
		this._max = _max;
	}
	
	/**
	 * Définir le numéro maximum
	 * @param _max Le numéro maximum
	 */
	public void setMax(String _max) {
		if (_max == null) {
			this._max = 0;
		}
		else {
			try {
				this._max = Integer.valueOf(_max);				
			}
			catch (NumberFormatException ex) {
				lg.error("Erreur, 'max' pas un nombre : " + _max);
				this._max = 0;
			}
		}
	}

	/**
	 * Retourner le champ à répéter
	 * @return Le champ
	 */
	public DefChamp getChampARepeter() {
		return champARepeter;
	}

	/**
	 * Définir le champ à répéter
	 * @param champARepeter Le champ
	 */
	public void setChampARepeter(DefChamp champARepeter) {
		this.champARepeter = champARepeter;
	}
	
}
