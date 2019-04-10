package models.AST;
import models.Visitors.Visitor;

public class IfStatNode extends Node {

	public IfStatNode(){
		super("");
	}


	public IfStatNode( Node relExprNode, Node ifStatBlockNode, Node elseStatBlockNode){
		super("");
		this.addChild(relExprNode);
		this.addChild(ifStatBlockNode);
		this.addChild(elseStatBlockNode);
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