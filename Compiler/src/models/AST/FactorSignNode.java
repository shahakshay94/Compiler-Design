package models.AST;

import models.Visitors.Visitor;

public class FactorSignNode extends Node {

	public FactorSignNode(String data){
		super(data);
	}


	public FactorSignNode(String data, Node factorNode){
		super(data); 
		this.addChild(factorNode);
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
