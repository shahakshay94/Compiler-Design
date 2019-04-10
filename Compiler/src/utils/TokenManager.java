package utils;

import models.Lexeme;
import models.StateTransition;
import models.TokenType;

import java.util.ArrayList;
import java.util.List;

public class TokenManager {
    private static TokenManager ourInstance = new TokenManager();

    public static TokenManager getInstance() {
        return ourInstance;
    }

    private TokenManager() {
    }

    public List<Lexeme> getPossibleTokenListFromInput(char input){
        List<Lexeme> lexemeList = new ArrayList<>();
        for (Lexeme t : Lexeme.values()) {
            if (t.isFind(input)) {
                lexemeList.add(t);
            }
        }
        return lexemeList;
    }

    public TokenType getTokenTypeFromString(String tokenType){
        for (TokenType t : TokenType.values()) {
            if (t.getTokenType().equals(tokenType)) {
                return t;
            }
        }
        return null;
    }

    public TokenType getErrorTypeFromGeneratedErrorToken(String token, List<StateTransition> currentStateTransitionList){
        if(token.equals("_")){
            return TokenType.INVALID_IDENTIFIER;
        }
        if(currentStateTransitionList!=null && !currentStateTransitionList.isEmpty()){
            String currentStateTransition =  currentStateTransitionList.get(0).getTransitionState();

            if(currentStateTransition.equals("Q2") ||currentStateTransition.equals("Q9") ||currentStateTransition.equals("Q11") ||currentStateTransition.equals("Q12")) {
                return TokenType.INVALID_NUMBER;
            }

        }
        return TokenType.INVALID_IDENTIFIER;
    }
}
