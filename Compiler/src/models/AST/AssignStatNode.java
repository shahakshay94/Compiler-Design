package models.AST;
import models.Visitors.Visitor;

public class AssignStatNode extends Node {
	
	public AssignStatNode(){
		super("=");
	}
		
	public AssignStatNode(Node parent){
		super("=", parent);
	}
	
	public AssignStatNode(Node leftChild, Node rightChild){
		super("="); 
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
