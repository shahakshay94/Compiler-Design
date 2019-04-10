package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class FParamListNode extends Node {
    public FParamListNode() {
        super("");
    }

    public FParamListNode(Node parent){
        super("", parent);
    }

    public FParamListNode(List<Node> listOfFParamsNodes){
        super("");
        for (Node child : listOfFParamsNodes)
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
