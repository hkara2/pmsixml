package fr.gpmsi.pmsixml;

/**
 * Petites fonctions utilitaires pour les objets
 * @author hkaradimas
 *
 */
public class ObjectUtils {

	/**
	 * Constructeur simple
	 */
	public ObjectUtils() {
	}

	/**
	 * Test d'égalité sûr entre deux objets.
	 * @param a objet a
	 * @param b objet b
	 * @return true si a est null et b aussi, ou sinon si a est égal à b
	 */
	public static boolean safeEquals(Object a, Object b) {
		if (a == null) return b == null;
		if (b == null) return false; else return a.equals(b);
	}
	
	/**
	 * Comparaison sûre entre deux objets qui ont tous deux un type qui est "Comparable"
	 * @param <T> le type à utiliser
	 * @param a objet a
	 * @param b objet b
	 * @return 0 si a et b sont null. -1 si a est null mais pas b. 1 si b est null mais pas a. Sinon retourne a.compareTo(b).
	 */
	public static <T extends Comparable<T>> int safeCompare(T a, T b) {
		if (a == null) {
			if (b == null) return 0;
			else return -1;
		}
		if (b == null) return 1;
		return a.compareTo(b);
	}
	
}
