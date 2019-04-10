package models;

import java.util.HashMap;

import exception.StateNotFoundException;

public class StateTransitionTable {

    private HashMap<String, State> stateTransitionTableMap = new HashMap<>();

    private static StateTransitionTable ourInstance = new StateTransitionTable();


    public static StateTransitionTable getInstance() {
        return ourInstance;
    }

    private StateTransitionTable() {
    }

    public void addStateTransition(String state, Lexeme lexeme, String transitionState, TokenType expectedTokenType, boolean isFinalState) {
        if (lexeme != null && stateTransitionTableMap.containsKey(state)) {
            stateTransitionTableMap.get(state).addStateTransition(lexeme, transitionState, expectedTokenType);
        } else {
            State tableState = new State(state, isFinalState);
            if (lexeme != null) {
                tableState.addStateTransition(lexeme, transitionState, expectedTokenType);
            }
            stateTransitionTableMap.put(state, tableState);
        }
    }

    public StateTransition getStateTransition(String currentState, Lexeme lexeme) throws StateNotFoundException {
        if (stateTransitionTableMap.containsKey(currentState)) {
            return stateTransitionTableMap.get(currentState).getStateTransition(lexeme);
        }
        throw new StateNotFoundException(currentState);
    }

    public State getStateInfo(String state) {
        return stateTransitionTableMap.get(state);
    }
}
