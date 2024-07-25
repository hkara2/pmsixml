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
	
	public int getMin() {
		return _min;
	}
	
	public void setMin(int _min) {
		this._min = _min;
	}
	
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
	
	public int getMax() {
		return _max;
	}
	
	public void setMax(int _max) {
		this._max = _max;
	}
	
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

	public DefChamp getChampARepeter() {
		return champARepeter;
	}

	public void setChampARepeter(DefChamp champARepeter) {
		this.champARepeter = champARepeter;
	}
	
	
}
