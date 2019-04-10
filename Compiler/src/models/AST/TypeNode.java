package models.AST;
import models.Visitors.Visitor;

public class TypeNode extends Node {
	
	public TypeNode(String data){
		super(data);
	}
	
	public TypeNode(String data, Node parent){
		super(data, parent);
	}
	
	/**
     * Every class that will be visited needs an accept method, which 
     * then calls the specific visit method in the visitor, achieving
     * double dispatch. 
     */   
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
