package fr.gpmsi.pmsixml.nx;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Définition de champ
 */
public class DefChamp {
	static Logger lg = LogManager.getLogger();
	
	String nom;
	String nomLong;
	char type;
	String vide = "0"; //valeur supplementaire pour considérer que le champ est vide. "0" par defaut. (? une autre valeur serait-elle utile ?)
	boolean traiterCrLf = false; //a true si on a TRAITER-CRLF="Y"
	
	String description;
	
	int lng; //longueur du champ
	int ech; //echelle lorsque c'est un nombre (par défaut 0)
	
	/** Constructeur simple */
	public DefChamp() {}
	
	/**
	 * Crée le champ, et ajuste la position p
	 * @param line La ligne à analyser
	 * @param p La position à mettre à jour
	 * @return Le Champ
	 */
	public Champ makeChamp(String line, Position p) {
		Champ c = new Champ();
		c.definition = this;
		String v = p.substring(line, lng);
		//lg.debug("in makeChamp v = '"+v+"'");
		advance(p);
		switch (type) {
		case 'N':
			if (v.trim().length() == 0) {
				c.valeurNum = null;
			}
			else {
				BigDecimal bd = new BigDecimal(v);
				bd = bd.divide(new BigDecimal(Math.pow(10, ech)));
				c.valeurNum = bd;
			}
			break;
		case 'D':
			if (v.length() != 8) {
				lg.error("Champ de date invalide : '"+v+"' en ligne "+Nx2Xml.lineNr);
			}
			else {
				if (v.equals("00000000") || v.trim().equals("")) c.valeurDate = null;
				else {
					String annee = v.substring(0, 4);
					String mois = v.substring(4, 6);
					String jour = v.substring(6, 8);
					Calendar cal = Calendar.getInstance();
					cal.set(Integer.valueOf(annee), Integer.valueOf(mois)-1, Integer.valueOf(jour)); //ne pas oublier que pour Calendar, le premier mois est le mois 0 ...
					//lg.debug("Valeur date lue pour champ "+nom+" : "+cal.getTime());
					c.valeurDate = cal.getTime();						
				}
			}
			break;
		case 'A':
			//fall through
		default:
			//par defaut on prend A
			c.valeurAlpha = v;
		}
		//lg.debug("returning "+this+", v '"+v+"', val '"+c+"'");
		return c;
	}
	
	/**
	 * Retourner la longueur du champe
	 * @return La longueur du champ
	 */
	public int getLng() {
		return lng;
	}

	/**
	 * Définir la longueur du champ
	 * @param lng La longueur du champ qui sera lue dans la ligne NX
	 */
	public void setLng(int lng) {
		this.lng = lng;
	}
	
	/**
	 * Définir la longueur du champ
	 * @param lng La longueur mais au format String. Si ce n'est pas un nombre correct, la longueur sera zéro.
	 */
	public void setLng(String lng) {
		if (lng == null) this.lng = 0;
		else {
			try {
				this.lng = Integer.valueOf(lng);
			}
			catch (NumberFormatException ex ) {
				lg.error("Erreur, lng pas un nombre : " + lng);
				this.lng = 0;
			}
		}
	}
	
	/**
	 * Retourner l'échelle du nombre qui est dans ce champ, c'est à dire le nombre de décimales après la virgule
	 * @return L'échelle du champ si c'est un nombre. Par défaut : 0.
	 */
	public int getEch() {
		return ech;
	}

	/**
	 * Définir l'échelle du nombre
	 * @param ech L'échelle
	 */
	public void setEch(int ech) {
		this.ech = ech;
	}
	
	/**
	 * Définir l'échelle du nombre
	 * @param ech L'échelle, mais sous forme de String
	 */
	public void setEch(String ech) {
		if (ech == null) this.ech = 0;
		else {
			try {
				this.ech = Integer.valueOf(ech);
			}
			catch (NumberFormatException ex ) {
				lg.error("Erreur, ech pas un nombre : " + ech);
				this.ech = 0;
			}
		}
	}
	
	/**
	 * Retourner une forme lisible de ce champ, qui inclut son nom.
	 */
	@Override
	public String toString() {
		return "Champ nom:'"+nom+"',typ:'"+type+"',lng:'"+lng+"'";
	}
	
	/**
	 * Tester l'égalité de cette définition de champ avec autre chose.
	 * Deux définitions sont égales si elles ont même nom, type, longueur et échelle.
	 * @param obj L'objet à tester. Si ce n'est pas une définition de champ, retourne false.
	 * @return true si les deux définitions sont égales
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefChamp)) return false;
		DefChamp c2 = (DefChamp) obj;
		return nom.equals(c2.nom) && type == c2.type && lng == c2.lng && ech == c2.ech;
	}
	
	/**
	 * Une fois le champ lu, avancer la position à l'aide de cette méthode.
	 * Cette méthode est déjà appelée dans {@link #makeChamp(String, Position)}
	 * donc il ne faut pas la rappeler une deuxième fois.
	 * @param p La position à avancer (de "lng").
	 */
	public void advance(Position p) { p.add(lng); }

	/**
	 * Retrourner la valeur qui signifie "vide" pour ce champ. Par défaut c'est zéro ("0").
	 * @return La valeur qui veut dire "vide"
	 */
	public String getVide() {
		return vide;
	}

	/**
	 * Définir la valeur qui signifie "vide". Laisser à "0" pour l'instant, 
	 * qui est la valeur par défaut.
	 * @param vide La valeur pour "vide"
	 */
	public void setVide(String vide) {
		this.vide = vide;
	}
}
