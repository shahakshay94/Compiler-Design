package models.AST;
import models.Visitors.Visitor;

public class FParamNode extends Node {

	public FParamNode(){
		super("");
	}


	public FParamNode(Node typeNode, Node idNode, Node dimListNode){
		super("");
		this.addChild(typeNode);
		this.addChild(idNode);
		this.addChild(dimListNode);
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