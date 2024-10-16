package fr.gpmsi.pmsixml;

/**
 * MONO : Stratégie de lecture mono-niveau, il n'y a pas de sous-niveau.
 * Pas de sous-groupe, uniquement des champs. 
 * Exemple : FICHCOMP, FICHSUP, VIDHOSP.
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyMONO 
extends FszNodeReadStrategy 
{

	/**
	 * Constructeur
	 */
	public FszNodeReadStrategyMONO() { super("MONO"); }

	@Override
	void readNode(InputString in, FszGroup node) 
			throws FieldParseException 
	{
		node.readLeafs(in);
	}

  @Override
  String readVersion(InputString in) {
    return "";
  }

  @Override
  String readMetaName(InputString in) {
    return "";
  }

}
