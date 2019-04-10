package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class FuncDefListNode extends Node {
	
	public FuncDefListNode(){
		super("");
	}
	
	public FuncDefListNode(Node parent){
		super("", parent);
	}
	
	public FuncDefListNode(List<Node> listOfFuncDefNodes){
		super("");
		for (Node child : listOfFuncDefNodes)
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