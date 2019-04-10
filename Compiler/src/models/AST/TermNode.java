package models.AST;
import models.Visitors.Visitor;

public class TermNode extends Node {

	public TermNode(){
		super("");
	}


	public TermNode(Node multopOrFactorNode){
		super("");
		this.addChild(multopOrFactorNode);
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