package fr.gpmsi.pmsixml;

/**
 * MONO : Stratégie de lecture mono-niveau.
 * Pas de sous-groupe, uniquement des champs. 
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyMONO 
extends FszNodeReadStrategy 
{

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
