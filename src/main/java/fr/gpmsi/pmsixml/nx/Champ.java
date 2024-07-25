package fr.gpmsi.pmsixml.nx;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Représente un champ qui a été lu depuis un fichier NX.
 * Chaque champ est rattaché à une définition de champ qui donne son nom, son type, sa longueur.
 * 
 */
public class Champ {
	static Logger lg = LogManager.getLogger();
	static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	DefChamp definition;
	String valeurAlpha;
	BigDecimal valeurNum;
	Date valeurDate;
	
	/**
	 * Joindre les deux champs.
	 * Pour valeurNum et valeurDate, on vérifie juste que les valeurs sont identiques.
	 * Pour valeurAlpha, on ajoute la valeur à la valeur précédente.
	 * @param c2 Le champ à joindre
	 */
	public void join(Champ c2) {
		if (!definition.equals(c2.definition)) {
			throw new IllegalArgumentException("Les deux champs ne sont pas du même type ("+definition+" vs "+c2.definition+")");
		}
		switch(definition.type) {
		case 'N':
			if (!valeurNum.equals(c2.valeurNum)) {
				lg.error("Tentative de joindre "+definition+" valeur "+valeurNum+" avec "+c2.valeurNum);
			}
			break;
		case 'D':
			if (!valeurDate.equals(c2.valeurDate)) {
				lg.error("Tentative de joindre "+definition+" valeur "+valeurDate+" avec "+c2.valeurDate);
			}
			break;
		case 'A':
			//fall through
		default:
			valeurAlpha += c2.valeurAlpha;
		}
	}
	
	/**
	 * Tester si le champ est vide.
	 * Le champ est vide si il est alpha et ne contient que des espaces, ou numérique et est vide.
	 * Si la définition contient VIDE="0", alors les valeurs numérique 0 sont considérées comme vide.
	 * Si le type est alpha et que VIDE n'est pas null, la valeur de  ne contient que des zéros.
	 * @return true si le champ est vide
	 */
	public boolean isEmpty() {
		String vide = definition.vide;
		//lg.debug("vide:'"+0+"'");
		switch(definition.type) {
		case 'D':
			return valeurDate == null;
		case 'N':
			//lg.debug("valeurNum: "+valeurNum);
			return valeurNum == null || ("0".equals(vide) && valeurNum.equals(BigDecimal.ZERO)); //ici "vide" ne marche que s'il est égal à "0" <- problème si on met une autre valeur pour définir "vide" pour les valeurs alpha ??
		case 'A':
			//fall through
		default:
			return valeurAlpha == null || valeurAlpha.trim().length() == 0 || (vide != null && vide.equals(valeurAlpha));
		}
	}
	
	/**
	 * Le retour de ligne dans le format NX est représenté par le caractère 0xB6 (182 en décimal)
	 * Cette méthode remplace tous les caractères 0xB6 en séquence CR+LF
	 * @param str Le texte à transformer
	 * @return Le texte avec les caractères 0xB6 remplacés par la séquence CR+LF
	 */
	public String traiterCrLf(String str) {
		if (str == null) return "";
		//lg.debug("Traitement CRLF '"+str+"' -> '"+str.replace("\u00B6", "\n")+"'");
		return str.replace("\u00B6", "\n"); //pour le XML il ne faut mettre que LF, sinon il insère une séquence &#13; !
	}

	/**
	 * Afficher le texte contenu dans ce champ, sans les espaces de fin.
	 * @return Le texte contenu dans ce champ
	 */
	@Override
	public String toString() {
		if (definition == null) {
			return valeurAlpha;
		}
		switch (definition.type) {
		case 'D': //date
			return valeurDate == null ? "" : sdf.format(valeurDate);
		case 'N': //num
			return valeurNum == null ? "" : valeurNum.toString();
		case 'A': //alpha
			//fall through
		default:
			if (valeurAlpha == null) return "";
			return definition.traiterCrLf ? traiterCrLf(valeurAlpha.trim()) : valeurAlpha.trim();
		}
	}
}
