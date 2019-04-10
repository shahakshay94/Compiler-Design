package models.AST;

import models.Visitors.Visitor;

public class ReadStatNode extends Node {

	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.
	 * Here, a variable declaration record is stored, which
	 * stores information aggregated by the VarDeclVisitor.
	 */

	public ReadStatNode(){
		super("");
	}

	public ReadStatNode(Node exprNode){
		super(""); 
		this.addChild(exprNode);
	}
	
	/**
	 * Every node should have an accept method, which 
	 * should call accept on its children to propagate
	 * the action of the visitor on its children. 
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}