package models.AST;

import models.Visitors.Visitor;

public class RelExprNode extends Node {

	public RelExprNode(){
		super("");
	}

	public RelExprNode(String data){
		super(data);
	}

	public RelExprNode(String data, Node parent){
		super(data, parent);
	}

	public RelExprNode(String data, Node leftChild, Node rightChild){
		super(data); 
		this.addChild(leftChild);
		this.addChild(rightChild);
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
