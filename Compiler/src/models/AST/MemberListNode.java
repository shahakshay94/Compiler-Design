package models.AST;

import java.util.List;

import models.Visitors.Visitor;

public class MemberListNode extends Node {
    public MemberListNode() {
        super("");
    }

    public MemberListNode(Node parent){
        super("", parent);
    }

    public MemberListNode(List<Node> listOfMemberDeclNodes){
        super("");
        for (Node child : listOfMemberDeclNodes)
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
