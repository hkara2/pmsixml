package fr.gpmsi.pmsixml;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AH : anohosp.
 * Strategie de lecture des Anohosp.
 * Utilisé dans le SSR/SMR et le MCO depuis la version 014.
 * C'est une stratégie de lecture mono-niveau, excepté qu'à la fin
 * il y a une lecture d'une liste de retour d'envoi de DMT, suivi
 * de la lecture d'une liste de DMT (Discipline Médico-Tarifaire).
 * Avant la version 014 c'était fichier "mono-niveau", donc déclaré 
 * avec S: MONO , maintenant il faut déclarer S: AH1.
 * @author hkaradimas
 *
 */
public class FszNodeReadStrategyAH1 
extends FszNodeReadStrategy 
{
    static Logger lg = LogManager.getLogger(FszNodeReadStrategyAH1.class);
  
    /**
     * Constructeur
     */
	public FszNodeReadStrategyAH1() { super("AH1");	}

	/**
	 * Lecture du compteur "DMT", et avec le nombre trouvé, lecture des sous-groupes "RDM" et "DMT"
	 */
	@Override
	void readNode(InputString in, FszGroup node)
		throws FieldParseException 
	{
		node.readLeafs(in);
		node.addChild(node.readSubGroups(in, "RDM", "DMT", false));
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
