package com.tool.data.dom;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

public class DomNode extends TreeNode {
	
	private static final long serialVersionUID = -3994429986518999945L;
	
	public DomNode(int id, StructureElement content, TreeNode father) {
		super(id, content, father);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Builds a DomNode object
	 * 
	 * @param id
	 * @param node a JSOUP Element node
	 * @param father
	 */
	public DomNode(int id, Element node, DomNode father) {
		super(id,null,father);
		DomElement content = new DomElement(node);
		this.setContent(content);
		
		ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		
		for(Element child : node.children()) {
			int maxId = this.getMaxId();
			DomNode c = new DomNode(maxId+1,child,this);
			children.add(c);
			this.setChildren(children);
		}
		
	}

}
