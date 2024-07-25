package fr.gpmsi.pmsixml.nx;

/**
 * La position dans le buffer, que l'on peut passer à une fonction pour l'incrémenter facilement.
 * Code non réentrant, ne pas utiliser dans du multithread.
 */
public class Position {
	private int value;
	
	/**
	 * Constructeur avac la valeur initiale de la position
	 * @param initialValue Valeur initiale
	 */
	public Position(int initialValue) { value = initialValue; }
	
	/** 
	 * Attribuer une nouvelle valeur de position
	 * @param newValue La nouvelle valeur
	 */
	public void setValue(int newValue) { value = newValue; }
	
	/** incrémenter */
	public void inc() { value++; }
	
	/**
	 * Ajouter une quantité pour faire avancer la position
	 * @param amount La quantité à ajouter
	 */
	public void add(int amount) { value += amount; }
	
	/**
	 * Retourner la valeur
	 * @return La valeur
	 */
	public int getValue() { return value; }
	
	/**
	 * raccourci pratique pour prendre str.substring(value, value+len)
	 * @param str La string
	 * @param len La longueur à prendre
	 * @return la sous-chaîne
	 */
	public String substring(String str, int len) { return str.substring(value, value+len); }
	
}
