package models.AST;
import models.Visitors.Visitor;

public class FuncDeclNode extends Node {

	public FuncDeclNode(){
		super("");
	}


	public FuncDeclNode(Node typeNode, Node idNode, Node fParamList){
		super("");
		this.addChild(typeNode);
		this.addChild(idNode);
		this.addChild(fParamList);
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