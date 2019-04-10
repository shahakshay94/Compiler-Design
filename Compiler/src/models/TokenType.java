package models;

public enum TokenType {

    ID("id"),
    INTEGER("number"),
    FLOAT("float"),
    OPERATOR("operator"),
    PUNCTUATION("punctuation"),
    RESERVED("reserved"),
    INVALID_IDENTIFIER("Invalid identifier"),
    INVALID_NUMBER("Invalid number");
    private String tokenType;
    TokenType(String tokenType) {
        this.tokenType =tokenType;
    }

    public String getTokenType() {
        return tokenType;
    }
}