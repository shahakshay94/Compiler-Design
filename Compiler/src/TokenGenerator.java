import java.util.ArrayList;
import java.util.List;

import exception.StateNotFoundException;
import models.Lexeme;
import models.State;
import models.StateTransition;
import models.StateTransitionTable;
import models.Token;
import models.TokenType;
import utils.BufferManager;
import utils.TokenManager;

public class TokenGenerator {

    private List<StateTransition> currentStateList = new ArrayList<>();
    private char lexeme;
    public static TokenGenerator instance;
    public StringBuilder generatedToken ;
    private boolean skipMultiLineCommnent=false;

    public static TokenGenerator getInstance() {
        if (instance == null) {
            instance = new TokenGenerator();
        }
        return instance;
    }

    private TokenGenerator() {

    }

    public void resetState() {
        currentStateList.clear();
        generatedToken = new StringBuilder();
        currentStateList.add(new StateTransition("Q0", null));
    }

    public Token getNextToken() {
        if (generatedToken==null /*lexeme not initialized*/) {
            lexeme = BufferManager.getInstance().getNextCharFromBuffer();
        }
        resetState();
        List<StateTransition> exploredNewState = new ArrayList<>();
        while (!BufferManager.getInstance().isEOF()) {
            exploredNewState.clear();
            for (StateTransition currentState : currentStateList) {
                List<Lexeme> lexemeList = TokenManager.getInstance().getPossibleTokenListFromInput(lexeme);
                for (Lexeme token : lexemeList) {
                    try {
                        StateTransition transitionState = StateTransitionTable.getInstance().getStateTransition(currentState.getTransitionState(), token);
                        if (transitionState != null) {
                            exploredNewState.add(transitionState);
                        }
                    } catch (StateNotFoundException e) {
                    }
                }
            }

            if (!exploredNewState.isEmpty()) {
                currentStateList.clear();
                currentStateList.addAll(exploredNewState);
                generatedToken.append(lexeme);
                lexeme = BufferManager.getInstance().getNextCharFromBuffer();
            } else {
                for (StateTransition currentState : currentStateList) {
                    State state = StateTransitionTable.getInstance().getStateInfo(currentState.getTransitionState());
                    if (state != null && state.isFinalState()) {
                        if (generatedToken.toString().equals("//")) {
                            BufferManager.getInstance().skipThisLine();
                            generatedToken =new StringBuilder();
                        }else if (generatedToken.toString().equals("/*")) {
                            skipMultiLineCommnent = true;
                            generatedToken =new StringBuilder();
                        }else if (generatedToken.toString().equals("*/")) {
                            generatedToken =new StringBuilder();
                            skipMultiLineCommnent = false;
                        } else if(skipMultiLineCommnent){
                            generatedToken =new StringBuilder();
                        }else {
                            Token token = new Token(currentState.getTokenTypeGenerated(), generatedToken.toString(), BufferManager.getInstance().getCurrentLineNumber(), BufferManager.getInstance().getColumnNumber());
                            return token;
                        }
                    }
                }

                // Manage invalid token
                if (generatedToken.length() == 0) {
                    // below code will work with whitespace also ..
                    // if lexeme not in the list of valid list then it will invalid token ..
                    if(!skipMultiLineCommnent) {
                        List<Lexeme> lexemeList = TokenManager.getInstance().getPossibleTokenListFromInput(lexeme);
                        if (!lexemeList.isEmpty()) {
                            generatedToken.append(lexeme);
                        }
                    }
                    lexeme = BufferManager.getInstance().getNextCharFromBuffer();
                }
                // check for invalid token ..
                if (generatedToken.length() > 0) {
                    TokenType tokenType = TokenManager.getInstance().getErrorTypeFromGeneratedErrorToken(generatedToken.toString(), currentStateList);
                    Token token = new Token(tokenType, generatedToken.toString(), BufferManager.getInstance().getCurrentLineNumber(), BufferManager.getInstance().getColumnNumber());
                    return token;
                }
                resetState();
            }
        }
        return null;
    }
}
