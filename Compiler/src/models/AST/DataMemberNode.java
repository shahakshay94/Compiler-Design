package models.AST;
import models.Visitors.Visitor;

public class DataMemberNode extends Node {

	public DataMemberNode(){
		super("");
	}


	public DataMemberNode(Node idNode, Node indexListNode){
		super("");
		this.addChild(idNode);
		this.addChild(indexListNode);
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