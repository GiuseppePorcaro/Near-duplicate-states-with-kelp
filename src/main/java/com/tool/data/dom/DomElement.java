package com.tool.data.dom;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import org.jsoup.nodes.Node;

/**
 * This class represents the "content" of each DOM node.
 * 
 * @author Luigi Libero Lucio Starace
 *
 */
public class DomElement extends StructureElement {
	
	private static final long serialVersionUID = -7322933486882129405L;

	private String type;
	private String ownText;
	private Map<String,String> attributes;
	private Node node;
	
	public DomElement() {
		
	}
	
	/**
	 * Builds a DomElement object from a Jsoup Element.
	 * 
	 * @param node a Jsoup Element node.
	 */
	public DomElement(Element node) {
		type = node.tagName();
		ownText = node.ownText();
		attributes = new HashMap<String,String>();
		for(Attribute a : node.attributes()) {
			attributes.put(a.getKey(), a.getValue());
		}
		this.node = node;
	}

	@Override
	public void setDataFromText(String structureElementDescription) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTextFromData() {
		return this.type;
		//return this.type + this.attributes.toString();
		//return node.outerHtml();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
