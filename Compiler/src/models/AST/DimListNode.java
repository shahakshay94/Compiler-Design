package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class DimListNode extends Node {
	
	public DimListNode(){
		super("");
	}
	
	public DimListNode(Node parent){
		super("", parent);
	}
	
	public DimListNode(List<Node> listOfDimNodes){
		super("");
		for (Node child : listOfDimNodes)
			this.addChild(child);
	}
	
	/**
	 * If a visitable class contains members that also may need 
	 * to be visited, it should make these members to accept
	 * the visitor before itself being visited. 
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
