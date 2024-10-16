package fr.gpmsi.pmsixml;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * VH : vidhosp.
 * Strategie de lecture des Vidhosp.
 * Utilisé dans le SSR/SMR et le MCO.
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyVH1 
extends FszNodeReadStrategy 
{
  static Logger lg = LogManager.getLogger(FszNodeReadStrategyVH1.class);
  
    /**
     * Constructeur
     */
	public FszNodeReadStrategyVH1() { super("VH1");	}

	@Override
	void readNode(InputString in, FszGroup node)
		throws FieldParseException 
	{
		node.readLeafs(in);
		node.addChild(node.readSubGroups(in, "DMT"));
	}

  @Override
  String readVersion(InputString in) {
    return in.line.substring(48, 52);
  }

  @Override
  String readMetaName(InputString in) {
    return "vidhosp"+readVersion(in);
  }

}
