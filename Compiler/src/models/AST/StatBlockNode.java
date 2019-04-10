package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class StatBlockNode extends Node {
		
	public StatBlockNode(){
		super("");
	}
	
	public StatBlockNode(Node parent){
		super("", parent);
	}
	
	public StatBlockNode(List<Node> listOfStatOrVarDeclNodes){
		super("");
		for (Node child : listOfStatOrVarDeclNodes)
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
