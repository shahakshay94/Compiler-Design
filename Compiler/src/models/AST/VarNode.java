package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class VarNode extends Node {
    public VarNode() {
        super("");
    }

    public VarNode(Node parent){
        super("", parent);
    }

    public VarNode(List<Node> listOfInherNodes){
        super("");
        for (Node child : listOfInherNodes)
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
