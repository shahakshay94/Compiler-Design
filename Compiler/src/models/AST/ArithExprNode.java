package models.AST;

import models.Visitors.Visitor;

public class ArithExprNode extends Node {

	public ArithExprNode(){
		super("");
	}

	public ArithExprNode(String data){
		super(data);
	}

	public ArithExprNode(String data, Node parent){
		super(data, parent);
	}

	public ArithExprNode(String data, Node leftChild, Node rightChild){
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
