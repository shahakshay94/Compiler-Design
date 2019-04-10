package models.AST;


import java.util.List;

import models.Visitors.Visitor;

public class ClassListNode extends Node {
	
	public ClassListNode(){
		super("");
	}
	
	public ClassListNode(Node parent){
		super("", parent);
	}
	
	public ClassListNode(List<Node> listOfClassNodes){
		super("");
		for (Node child : listOfClassNodes)
			this.addChild(child);
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