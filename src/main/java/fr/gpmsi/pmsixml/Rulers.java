package fr.gpmsi.pmsixml;

/**
 * Dessiner une règle. Utile pour vérifier les positions des champs dans un listing.
 * @author hkaradimas
 *
 */
public class Rulers {

	public Rulers() {
	}

	/**
	 * Make a ruler (a series of numbers to count chars)
	 * @param size The size of the ruler (= the end)
	 * @param start The start of the ruler (0 or 1)
	 * @param every When to increment (every 1, 10, 100, etc.)
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
	
	public static final String makeRuler(int size) { return makeRuler(size, 0, 1); }
	
}
