package fr.gpmsi.pmsixml;

/**
 * NX1 : lecture des informations de champ NX.
 * Pour la lecture d'un enregistrement NX il s'agit juste de lire les
 * champs simples.
 * N.B. Cette classe n'est plus très utile depuis 2024 où il y a eu la création du package fr.gpmsi.pmxixml.nx
 * @deprecated Utiliser plutôt {@link fr.gpmsi.pmsixml.nx.Nx2Xml}
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyNX1
extends FszNodeReadStrategy 
{

	/** Constructeur */
	public FszNodeReadStrategyNX1() { super("NX"); }

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
