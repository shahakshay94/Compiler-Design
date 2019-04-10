package models;

public enum ASTNodeType {
    ID_NODE("Id"),
    CLASS_DECL("ClassDec"),;


    public String getType() {
        return type;
    }

    private String type;

    ASTNodeType(String type) {

        this.type = type;
    }
}
