package models.AST;
import models.Visitors.Visitor;

public class VarElementNode extends Node {

	public VarElementNode(){
		super("");
	}


	public VarElementNode(Node dataMemberOrFCallNode){
		super("");
		this.addChild(dataMemberOrFCallNode);
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