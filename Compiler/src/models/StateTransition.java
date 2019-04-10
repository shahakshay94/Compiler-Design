package models;

public class StateTransition {
    private String transitionState;
    private TokenType tokenTypeGenerated;

    public StateTransition(String transitionState, TokenType tokenTypeGenerated) {
        this.transitionState = transitionState;
        this.tokenTypeGenerated = tokenTypeGenerated;
    }

    public String getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(String transitionState) {
        this.transitionState = transitionState;
    }

    public TokenType getTokenTypeGenerated() {
        return tokenTypeGenerated;
    }

    public void setTokenTypeGenerated(TokenType tokenTypeGenerated) {
        this.tokenTypeGenerated = tokenTypeGenerated;
    }
}
