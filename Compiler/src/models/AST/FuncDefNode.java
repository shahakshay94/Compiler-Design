package models.AST;

import models.Visitors.Visitor;

public class FuncDefNode extends Node {
		
	/**
	 * Some intermediate nodes may contain additional members
	 * that store information aggregated by specific visitors.  
	 * Here, a variable declaration record is stored, which 
	 * stores information aggregated by the VarDeclVisitor. 
	 * In the project, this record would be added to a symbol 
	 * table.  
	 */

	public FuncDefNode(){
		super("");
	}
	
	public FuncDefNode(Node parent){
		super("", parent);
	}
	
	public FuncDefNode(Node type, Node scopeSpec, Node id, Node paramList, Node statBlock){
		super("");
		this.addChild(type);
		this.addChild(scopeSpec);
		this.addChild(id);
		this.addChild(paramList);		
		this.addChild(statBlock);
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