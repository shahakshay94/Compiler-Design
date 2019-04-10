package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class ClassNode extends Node {
		
	public ClassNode(){
		super("");
	}
	
	public ClassNode(Node parent){
		super("", parent);
	}
	
	public ClassNode(Node id, List<Node> listOfClassMemberNodes){
		super("");
		this.addChild(id);
		for (Node child : listOfClassMemberNodes)
			this.addChild(child);
	}
	public ClassNode(Node id, Node inherListNode,Node memberListNode){
		super("");
		this.addChild(id);
		this.addChild(inherListNode);
		this.addChild(memberListNode);
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