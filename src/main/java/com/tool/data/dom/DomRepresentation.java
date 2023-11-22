package com.tool.data.dom;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

/**
 * Tree Representation used to represent the Document Object Model (DOM) 
 * of a web page. 
 * 
 * @author Luigi Libero Lucio Starace
 *
 */
public class DomRepresentation extends TreeRepresentation {
	
	private static final long serialVersionUID = 6548676516847354135L;  // unique id
	
	
	public DomRepresentation() {
		
	}
	
	/**
	 * Build a DomRepresentation from a DomNode
	 * 
	 * @param root Root of the Tree
	 */
	public DomRepresentation(DomNode root) {
		this.root = root;
		this.orderedNodeSetByLabel = root.getAllNodes();
		this.orderedNodeSetByProduction = root.getAllNodes();
		this.orderedNodeSetByProductionIgnoringLeaves = root.getAllNodes();
		this.orderedNodeSetByProductionIgnoringLeaves.removeAll(root.getLeaves());	
	}
	
	@Override
	public TreeRepresentation clone() {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean equals(Object representation) {
		// TODO Auto-generated method stub
		return super.equals(representation);
	}

	@Override
	public void updateTree() {
		// TODO Auto-generated method stub
		super.updateTree();
	}

	@Override
	public List<TreeNode> getAllNodes() {
		// TODO Auto-generated method stub
		return super.getAllNodes();
	}

	@Override
	public Integer getMaxId() {
		// TODO Auto-generated method stub
		return super.getMaxId();
	}

	@Override
	public List<TreeNode> getOrderedNodeSetByLabel() {
		// TODO Auto-generated method stub
		return super.getOrderedNodeSetByLabel();
	}

	@Override
	public List<TreeNode> getOrderedNodeSetByProduction() {
		// TODO Auto-generated method stub
		return super.getOrderedNodeSetByProduction();
	}

	@Override
	public List<TreeNode> getOrderedNodeSetByProductionIgnoringLeaves() {
		// TODO Auto-generated method stub
		return super.getOrderedNodeSetByProductionIgnoringLeaves();
	}

	@Override
	public TreeNode getRoot() {
		// TODO Auto-generated method stub
		return super.getRoot();
	}

	@Override
	public String getTextFromData() {
		// TODO Auto-generated method stub
		return super.getTextFromData();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public void setDataFromText(String representationDescription) throws Exception {
		// TODO Auto-generated method stub
		super.setDataFromText(representationDescription);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public String getTextualEnrichedTree() {
		// TODO Auto-generated method stub
		return super.getTextualEnrichedTree();
	}

	@Override
	public List<TreeNode> getNodesWithContentType(Class<? extends StructureElement> clazz) {
		// TODO Auto-generated method stub
		return super.getNodesWithContentType(clazz);
	}

	@Override
	public List<TreeNode> getLeaves() {
		// TODO Auto-generated method stub
		return super.getLeaves();
	}

	@Override
	public List<TreeNode> getPreLeafNodes(int generationHops) {
		// TODO Auto-generated method stub
		return super.getPreLeafNodes(generationHops);
	}

	@Override
	public boolean isCompatible(Representation rep) {
		// TODO Auto-generated method stub
		return super.isCompatible(rep);
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return super.getNumberOfNodes();
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return super.getHeight();
	}

	@Override
	public int getBranchingFactor() {
		// TODO Auto-generated method stub
		return super.getBranchingFactor();
	}

}
