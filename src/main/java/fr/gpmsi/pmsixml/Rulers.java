package fr.gpmsi.pmsixml;

/**
 * Dessiner une règle. Utile pour vérifier les positions des champs dans un listing.
 * @author hkaradimas
 *
 */
public class Rulers {

    /**
     * Constructeur simple
     */
	public Rulers() {
	}

	/**
	 * Faire une "règle" (une série de nombre qui aidera à compter les caractères)
	 * @param size La taille de la règle en nombre de caractères
	 * @param start Le début de la règle (0 ou 1)
	 * @param every La taille de l'incrément (tous les 1, 10, 100, etc.)
	 * @return La règle
	 */
	public static final String makeRuler(int size, int start, int every) {
		StringBuffer sb = new StringBuffer();
		int n = start;
		for (int i = start; i < size; i++) {
			if (i % every == 0) {
				sb.append(String.valueOf(n));
				n++;
				if (n > 9) n = 0;
			}
			else {
				sb.append(' ');
			}
		}//for
		return sb.toString();
	}
	
	/**
	 * Faire un texte qui peut servir de "règle", à copier coller au dessus
	 * d'une ligne de RSS par exemple, pour vérifier tous les débuts de
	 * champs.
	 * Débute à 0, et met un chiffre tous les 10 caractères
	 * @param size La largeur de la règle en caractères
	 * @return Le texte qui représente la règle
	 */
	public static final String makeRuler(int size) { return makeRuler(size, 0, 1); }
	
}
