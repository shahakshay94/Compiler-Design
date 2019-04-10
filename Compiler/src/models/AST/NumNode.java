package models.AST;
import models.Visitors.Visitor;

public class NumNode extends Node {
	
	public NumNode(String data){
		super(data);
	}
	
	public NumNode(String data, Node parent){
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
