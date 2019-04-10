package models.AST;
import models.Visitors.Visitor;

public class FCallNode extends Node {

	public FCallNode(){
		super("");
	}


	public FCallNode(Node idNode, Node aParamsNode){
		super("");
		this.addChild(idNode);
		this.addChild(aParamsNode);
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