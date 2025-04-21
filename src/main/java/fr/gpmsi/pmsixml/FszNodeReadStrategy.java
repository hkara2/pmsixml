package fr.gpmsi.pmsixml;

import java.util.HashMap;
import java.util.Set;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ancetre des strategies de lecture.
 * La strategie de lecture est en général d'essayer de deviner une version de format,
 * puis de charger la métadonnée correspondante.
 * Ensuite on lit le groupe principal, puis les sous-groupes en fonction de ce qui a été
 * lu avant.
 * Pour le format NX c'est différent car la sélection du groupe à utiliser dépend directement
 * des 3 à 6 premiers caractères.
 * @author hkaradimas
 *
 */
//@SuppressWarnings("deprecation")
public abstract class FszNodeReadStrategy 
{
	static Logger lg = LogManager.getLogger(FszNodeReadStrategy.class);
	
	private static HashMap<String, FszNodeReadStrategy> strategiesByName;
	String name;

	//déclaration de toutes les stratégies utilisables par le système.
	//L'utilisateur peut rajouter les siennes ensuite si il veut
	static {
		strategiesByName = new HashMap<>();
		declare(new FszNodeReadStrategyMONO());
		declare(new FszNodeReadStrategyRSA1());
        declare(new FszNodeReadStrategyRSS1());
        declare(new FszNodeReadStrategyRHS1());
        declare(new FszNodeReadStrategyVH1());
        //declare(new FszNodeReadStrategyNX1()); //hk 230115
        declare(new FszNodeReadStrategyAH1()); //hk 250420
	}
	
	/**
	 * Ajouter une nouvelle stratégie de lecture
	 * @param strategy la stratégie à ajouter
	 */
	public static void declare(FszNodeReadStrategy strategy) {
		strategiesByName.put(strategy.getName(), strategy);
	}
	
	/**
	 * Trouver la stratégie de lecture qui porte le nom donné
	 * @param name Le nom de la stratégie à rechercher
	 * @return La stratégie ou null si non trouvé
	 */
	public static FszNodeReadStrategy findStrategy(String name) {
		return strategiesByName.get(name);
	}
	
	/**
	 * Retourner les noms des stratégies disponibles
	 * @return l'ensemble des noms disponibles
	 */
	public static Set<String> getAvailableStrategyNames() {
		return strategiesByName.keySet();
	}
	
	/**
	 * Constructeur
	 * @param name Le nom de la stratégie
	 */
	public FszNodeReadStrategy(String name) { this.name = name;	}

	/**
	 * Lire le noeud depuis la chaîne d'entrée en utilisant cette stratégie.
	 * Implémentée dans les classes dérivées.
	 * @param in le InputString à lire
	 * @param node Le noeud de groupe dans lequel mettre les éléments (champs, groupes) lus
	 * @throws FieldParseException si il y a une erreur lors de l'analyse de la chaîne d'entrée
	 */
	abstract void readNode(InputString in, FszGroup node) throws FieldParseException;
	
	/**
	 * Lire le numéro de version de métadonnées depuis une chaîne d'entrée.
	 * Utile pour savoir quelle métadonnée lire.
	 * Par ex. pour un RSS cette information se trouve de la colonne 10 à la colonne 12.
	 * @param in le InputString à lire
	 * @return l'information de version ou null si il n'y a pas d'information de version disponible.
	 */
	abstract String readVersion(InputString in);
	
	/**
	 * Déterminer le nom à utiliser pour les formats prédéfinis (rsa, rss, vh, etc.) en lisant 
	 * le descripteur de métadonnées correct.
	 * @param in le InputString à lire
	 * @return Le nom à utiliser (par ex. rss018)
	 */
	abstract String readMetaName(InputString in);
	
	/** Le nom de la stratégie (par ex "MONO" est utilisé pour FszNodeReadStrategyMONO
	 * 
	 * @return le nomb de la stratégie qui sera utilisée
	 */
	public String getName() {	return name; }

	/**
	 * définir le nom de la stratégie de lecture qui sera utilisée
	 * @param name le nom de la stratégie
	 */
	public void setName(String name) { this.name = name; }

}
