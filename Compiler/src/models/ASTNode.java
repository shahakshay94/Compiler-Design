package models;

public class ASTNode {

    public ASTNode parent;
    public ASTNode sibling;
    public String nodeType;
    public String data;
    public ASTNode firstChild;

    public ASTNode(String nodeType) {
        this.nodeType = nodeType;
    }

    public ASTNode(String nodeType, String data) {
        this.nodeType = nodeType;
        this.data = data;
    }
}
