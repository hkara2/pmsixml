package fr.gpmsi.pmsixml.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Petite classe utilitaire pour aider dans la manipulation des noeuds Node du XML.
 */
public class NodeUtils {

    /**
     * Renvoyer l'attribut
     * @param map Une Map avec les Node(s)
     * @param name Le nom de l'attribut
     * @return La valeur de l'attribut ou null si l'attribut n'existe pas
     */
	public static final String getAttribute(NamedNodeMap map, String name) {
		if (map == null) return null;
		Node attNode = map.getNamedItem(name);
		if (attNode == null) return null;
		return attNode.getNodeValue();	
	}
}
