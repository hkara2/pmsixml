package fr.gpmsi.pmsixml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RHS1 : Résumés Hebdomadaires Standardisés.
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyRHS1 
extends FszNodeReadStrategy 
{
  static Logger lg = LogManager.getLogger(FszNodeReadStrategyRSS1.class);
  
	public FszNodeReadStrategyRHS1() { super("RHS1");	}

	@Override
	void readNode(InputString in, FszGroup node)
		throws FieldParseException 
	{
		node.readLeafs(in);
		node.addChild(node.readSubGroups(in, "DA"));
		node.addChild(node.readSubGroups(in, "ACS"));
		node.addChild(node.readSubGroups(in, "ACC"));
	}

  @Override
  String readVersion(InputString in) {
    return in.line.substring(9, 12);
  }

  @Override
  String readMetaName(InputString in) {
    return "rhs"+readVersion(in);
  }

}
