package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class IndexListNode extends Node {
    public IndexListNode() {
        super("");
    }

    public IndexListNode(Node parent){
        super("", parent);
    }

    public IndexListNode(List<Node> listOfArithExpr){
        super("");
        for (Node child : listOfArithExpr)
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
