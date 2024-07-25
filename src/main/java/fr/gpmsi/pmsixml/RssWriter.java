package fr.gpmsi.pmsixml;

/**
 * Ecrire un RSS à partir d'un FszGroup.
 * @author hkaradimas
 *
 */
public class RssWriter {

	public RssWriter() {
	}

	/**
	 * @deprecated Utiliser writeRum à la place
     * @param rum _
     * @param sb _
     * @throws FieldParseException _
     * @throws FieldSizeException _
	 * 
	 */
    public void writeRss(FszGroup rum, StringBuffer sb) 
    throws FieldParseException, FieldSizeException
    {
      writeRum(rum, sb);
    }
    
    /**
     * Ecrire le rum dans le buffer
     * @param rum Le {@link FszGroup} à utiliser avec le RUM
     * @param sb Le StringBuffer dans lequel envoyer le RUM
     * @throws FieldParseException _
     * @throws FieldSizeException _
     */
	public void writeRum(FszGroup rum, StringBuffer sb) 
	throws FieldParseException, FieldSizeException
	{
		FszNode das = rum.getChild("DA");
		int dasCount = das.getChildCount();
		FszField ndaNd = (FszField) rum.getChild("NDA");
		ndaNd.setValue(Integer.toString(dasCount));
		FszNode dads = rum.getChild("DAD");
		int dadsCount = dads.getChildCount();
		FszField ndadNd = (FszField) rum.getChild("NDAD");
		ndadNd.setValue(Integer.toString(dadsCount));
		FszNode zas = rum.getChild("ZA");
		int zasCount = zas.getChildCount();
		FszField nzaNd = (FszField) rum.getChild("NZA");
		nzaNd.setValue(Integer.toString(zasCount));
		//now emit the text proper
		rum.toText(sb);
		for (FszNode child : das.getChildren()) child.toText(sb);
		for (FszNode child : dads.getChildren()) child.toText(sb);		
		for (FszNode child : zas.getChildren()) child.toText(sb);
	}
	
	/**
	 * Envoyer le RUM en tant que texte.
	 * @param rum Le {@link FszGroup} qui représente la ligne de RUM
	 * @return Le texte du RUM tel qu'il doit être dans le fichier de RSS
	 * @throws FieldParseException _
	 * @throws FieldSizeException _
	 */
	public String rumToString(FszGroup rum) 
	    throws FieldParseException, FieldSizeException 
	{
	  StringBuffer sb = new StringBuffer();
	  writeRum(rum, sb);
	  return sb.toString();
	}
}
