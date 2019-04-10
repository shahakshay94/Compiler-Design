package models.AST;

import models.Visitors.Visitor;

public class FactorNode extends Node {

	public FactorNode(String data){
		super(data);
	}


	public FactorNode(String data, Node childNode){
		super(data); 
		this.addChild(childNode);
	}
	public FactorNode(Node childNode){
		super("");
		this.addChild(childNode);
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
