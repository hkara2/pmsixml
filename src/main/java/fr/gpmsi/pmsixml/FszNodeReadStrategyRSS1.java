package fr.gpmsi.pmsixml;

import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;

/**
 * RSS : résumés standardisés de sortie
 * Utilisé dans le MCO.
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyRSS1 
extends FszNodeReadStrategy 
{
  static Logger lg = LogManager.getLogger(FszNodeReadStrategyRSS1.class);
  
  /**
   * Constructeur
   */
  public FszNodeReadStrategyRSS1() { super("RSS1");  }

  @Override
  void readNode(InputString in, FszGroup node)
    throws FieldParseException 
  {
    node.readLeafs(in);
    node.addChild(node.readSubGroups(in, "DA"));
    node.addChild(node.readSubGroups(in, "DAD"));
    node.addChild(node.readSubGroups(in, "ZA"));
  }

  @Override
  String readVersion(InputString in) {
    return in.line.substring(9, 12);
  }

  @Override
  String readMetaName(InputString in) {
    return "rss"+readVersion(in);
  }

}


