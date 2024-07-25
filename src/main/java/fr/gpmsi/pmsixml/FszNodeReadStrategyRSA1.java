package fr.gpmsi.pmsixml;

import java.util.List;

/**
 * RSA : résumés de sortie anonymisés
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyRSA1
extends FszNodeReadStrategy 
{

  public FszNodeReadStrategyRSA1() { super("RSA1");  }

  @Override
  void readNode(InputString in, FszGroup node)
      throws FieldParseException 
  {
    //FszGroupMeta meta = (FszGroupMeta) node.getMeta();
    node.readLeafs(in);
    FszGroup ags = node.readSubGroups(in, "AG", true); //AG n'existe plus à partir du RSA V.211
    if (ags != null) node.addChild(ags);
    FszGroup srs = node.readSubGroups(in, "SR");
    if (srs != null) node.addChild(srs);
    FszGroup rus = node.readSubGroups(in, "RU");
    if (rus != null) {
      node.addChild(rus);
      List<FszNode> childRus = rus.getChildren();
      lg.debug("Nombre de RU : "+childRus.size());
      lg.debug("Lecture des DA");
      for (FszNode childRuNd : childRus) {
        FszGroup childRu = (FszGroup) childRuNd;
        FszGroup das = ((FszGroup)childRu).readSubGroups(in, "DA");
        childRu.addChild(das);        
      }
      lg.debug("Lecture des ZA");
      for (FszNode childRuNd : childRus) {
        FszGroup childRu = (FszGroup) childRuNd;
        FszGroup zas = ((FszGroup)childRu).readSubGroups(in, "ZA");
        childRu.addChild(zas);
      }
    }
    else {
      lg.error("Pas trouve de RU !");
    }
  }

  @Override
  String readVersion(InputString in) {
    return in.line.substring(9, 12);
  }

  @Override
  String readMetaName(InputString in) {
    return "rsa"+readVersion(in);
  }

}
