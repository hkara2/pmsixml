package fr.gpmsi.pmsixml.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NodeUtils {

	public static final String getAttribute(NamedNodeMap map, String name) {
		if (map == null) return null;
		Node attNode = map.getNamedItem(name);
		if (attNode == null) return null;
		return attNode.getNodeValue();	
	}
	
	public static final String getText(Node parent) {
		return null;
	}
}
