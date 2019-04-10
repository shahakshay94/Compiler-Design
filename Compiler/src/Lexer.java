import exception.StateNotFoundException;
import models.*;
import utils.BufferManager;
import utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private List<StateTransition> currentStateList = new ArrayList<>();
    private List<Token> generatedTokenList;
    private boolean skipMultiLineCommnent;

    public Lexer() {
        initialize();
    }

    private void initialize() {
        generatedTokenList = new ArrayList<>();
    }

    public List<Token> getGeneratedTokenList() {
        return generatedTokenList;
    }

    public void generateNextToken() {
        currentStateList.clear();
        currentStateList.add(new StateTransition("Q0", null));
        char lexeme = BufferManager.getInstance().getNextCharFromBuffer();
        StringBuilder generatedToken = new StringBuilder();
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
                boolean isTokenFound = false;
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
                        }else{
                            Token token = new Token(currentState.getTokenTypeGenerated(), generatedToken.toString(), BufferManager.getInstance().getCurrentLineNumber(), BufferManager.getInstance().getColumnNumber());
                            generatedTokenList.add(token);
                            isTokenFound = true;
                        }

                    }
                }

                // Manage invalid token
                if (!isTokenFound) {
                    if (generatedToken.length() == 0) {

                        // below code will work with whitespace also ..
                        // if lexeme not in the list of valid list then it will invalid token ..
                        List<Lexeme> lexemeList = TokenManager.getInstance().getPossibleTokenListFromInput(lexeme);
                        if (!lexemeList.isEmpty()) {
                            generatedToken.append(lexeme);
                        }
                        lexeme = BufferManager.getInstance().getNextCharFromBuffer();
                    }
                    // check for invalid token ..
                    if (generatedToken.length() > 0) {
                        TokenType tokenType = TokenManager.getInstance().getErrorTypeFromGeneratedErrorToken(generatedToken.toString(), currentStateList);
                        Token token = new Token(tokenType, generatedToken.toString(), BufferManager.getInstance().getCurrentLineNumber(), BufferManager.getInstance().getColumnNumber());
                        generatedTokenList.add(token);
                    }
                }
                generatedToken = new StringBuilder();

                currentStateList.clear();
                currentStateList.add(new StateTransition("Q0", null));
            }
        }
    }

}
