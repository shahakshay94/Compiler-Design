package models.AST;
import models.Visitors.Visitor;

public class ScopeSpecNode extends IdNode{

	public ScopeSpecNode(String data){
		super(data);
	}

	public ScopeSpecNode(String data, Node parent){
		super(data, parent);
	}

	public ScopeSpecNode(String data, String type){
		super(data, type);
	}
	
	/**
	 * Every class that may be visited by any visitor needs
	 * to implement the accept method, that calls the appropriate
	 * visit method in the passed visitor, achieving double
	 * dispatch. 
	 * 
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
