package models.AST;
import models.Visitors.Visitor;

public class AddOpNode extends Node {
	
	public AddOpNode(String data){
		super(data);
	}
	
	public AddOpNode(String data, Node parent){
		super(data, parent);
	}
	
	public AddOpNode(String data, Node leftChild, Node rightChild){
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