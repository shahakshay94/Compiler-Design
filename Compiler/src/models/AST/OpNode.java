package models.AST;

import models.Visitors.Visitor;

public class OpNode extends Node {
    public OpNode(String data) {
        super(data);
    }
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
