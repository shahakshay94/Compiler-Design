package models;

public enum ReservedToken {
    AND ("&&","and"),
    NOT ("!","not"),
    OR ("or","or"),
    ID("if","if"),
    THEN("then","then"),
    ELSE("else","else"),
    FOR("for","for"),
    CLASS("class","class"),
    INT("integer","integer"),
    FLOAT("float","float"),
    READ("read","read"),
    WRITE("write","write"),
    RETURN("return","return"),
    MAIN("main","main"),
    EQ ("==","eq"),
    NEQ ("<>","neq"),
    LT ("<","lt"),
    GT (">","gt"),
    LEQ ("<=","leq"),
    GEQ (">=","geq"),
    SR ("::","sr");

    private String reservedTokenType;
    private String tokenType;

    ReservedToken(String tokenType, String reservedTokenType) {
        this.reservedTokenType = reservedTokenType;
        this.tokenType = tokenType;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getReservedTokenType() {
        return reservedTokenType;
    }
}