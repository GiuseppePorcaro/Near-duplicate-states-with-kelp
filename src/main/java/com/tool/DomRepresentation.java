package com.tool;

import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

import java.util.List;

public class DomRepresentation extends TreeRepresentation {

    public DomRepresentation() {

    }

    public DomRepresentation(TreeNode root) {
        this.root = root;
        this.orderedNodeSetByLabel = root.getAllNodes();
        this.orderedNodeSetByProduction = root.getAllNodes();
        this.orderedNodeSetByProductionIgnoringLeaves = root.getAllNodes();
        this.orderedNodeSetByProductionIgnoringLeaves.removeAll(root.getLeaves());
    }

    @Override
    public TreeRepresentation clone() {
        return super.clone();
    }

    @Override
    public boolean equals(Object representation) {
        return super.equals(representation);
    }

    @Override
    public void updateTree() {
        super.updateTree();
    }

    @Override
    public List<TreeNode> getAllNodes() {
        return super.getAllNodes();
    }

    @Override
    public Integer getMaxId() {
        return super.getMaxId();
    }

    @Override
    public List<TreeNode> getOrderedNodeSetByLabel() {
        return super.getOrderedNodeSetByLabel();
    }

    @Override
    public List<TreeNode> getOrderedNodeSetByProduction() {
        return super.getOrderedNodeSetByProduction();
    }

    @Override
    public List<TreeNode> getOrderedNodeSetByProductionIgnoringLeaves() {
        return super.getOrderedNodeSetByProductionIgnoringLeaves();
    }

    @Override
    public TreeNode getRoot() {
        return super.getRoot();
    }

    @Override
    public String getTextFromData() {
        return super.getTextFromData();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void setDataFromText(String representationDescription) throws Exception {
        super.setDataFromText(representationDescription);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getTextualEnrichedTree() {
        return super.getTextualEnrichedTree();
    }

    @Override
    public List<TreeNode> getLeaves() {
        return super.getLeaves();
    }

    @Override
    public List<TreeNode> getPreLeafNodes(int generationHops) {
        return super.getPreLeafNodes(generationHops);
    }

    @Override
    public boolean isCompatible(Representation rep) {
        return super.isCompatible(rep);
    }

    @Override
    public int getNumberOfNodes() {
        return super.getNumberOfNodes();
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @Override
    public int getBranchingFactor() {
        return super.getBranchingFactor();
    }


}
