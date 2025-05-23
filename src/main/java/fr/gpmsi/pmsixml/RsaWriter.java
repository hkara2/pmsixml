package fr.gpmsi.pmsixml;

import java.util.List;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;

/**
 * Ecriture d'un fichier RSA à partir d'un FszGroup
 * @author hkaradimas
 *
 */
public class RsaWriter {
  static Logger lg = LogManager.getLogger(RsaWriter.class);
  /**
   * Mettre à TRUE si des traces détaillées sont souhaitées pendant le développement
   */
  public static final boolean DEBUG = false;
  
  /**
   * Constructeur simple
   */
	public RsaWriter() {
	}

	/**
	 * Mettre à jour le compteur du parent avec le nombre des enfants de g.
	 * @param parent Le groupe parent
	 * @param g Le groupe
	 * @param counterName Le nom du compteur
	 */
	public void updateCounter(FszGroup parent, FszGroup g, String counterName) {
		List<FszNode> children = g.getChildren();
		int childCount = children.size();
		FszField counter = (FszField) parent.getChild(counterName);
		if (counter == null) {
			lg.error("Compteur non trouve : "+counterName);
		}
		else {
		  counter.setValue(Integer.toString(childCount));
		}
	}
	
	/**
	 * Ecrire le groupe dans le StringBuffer
	 * @param g Le groupe
	 * @param sb Le StringBuffer
	 * @throws FieldSizeException Si erreur de taille de champ
	 */
	public void writeGroup(FszGroup g, StringBuffer sb)
			throws FieldSizeException
	{
		List<FszNode> children = g.getChildren();
		for (FszNode child : children) child.toText(sb);
	}
	
	/**
	 * Ecrire le RSA dans le StringBuffer
	 * @param rsa Le RSA
	 * @param sb Le StringBuffer
	 * @throws FieldSizeException Si erreur de taille de champ
	 */
	public void writeRsa(FszGroup rsa, StringBuffer sb)
			throws FieldSizeException
	{
		//read and emit in order : AG SR RU DA ZA
		FszGroup ag = (FszGroup) rsa.getChild("AG");
		FszGroup sr = (FszGroup) rsa.getChild("SR");
		FszGroup ru = (FszGroup) rsa.getChild("RU");
		updateCounter(rsa, ag, "NAG");
		updateCounter(rsa, sr, "NSR");
		updateCounter(rsa, ru, "NBRUM");
		for (FszNode chruNd : ru.getChildren()) {
			FszGroup chru = (FszGroup) chruNd;
			FszGroup da = (FszGroup) chru.getChild("DA");
			int daCount = da.getChildCount();
			FszField nda = (FszField) chru.getChild("NDA");
			nda.setValue(Integer.toString(daCount));
			FszGroup za = (FszGroup) chru.getChild("ZA");
			int zaCount = za.getChildCount();
			FszField nza = (FszField) chru.getChild("NZA");
			nza.setValue(Integer.toString(zaCount));
			if (DEBUG) System.out.println("RU:"+ru+",nda:"+daCount+",nza:"+zaCount);
		}
		rsa.toText(sb);
		writeGroup(ag, sb);
		writeGroup(sr, sb);
		writeGroup(ru, sb);
		for (FszNode chruNd : ru.getChildren()) {
			FszGroup chru = (FszGroup) chruNd;
			FszGroup da = (FszGroup) chru.getChild("DA");
			writeGroup(da, sb);
		}
		for (FszNode chruNd : ru.getChildren()) {
			FszGroup chru = (FszGroup) chruNd;
			FszGroup za = (FszGroup) chru.getChild("ZA");
			writeGroup(za, sb);
		}		
	}
	
}
