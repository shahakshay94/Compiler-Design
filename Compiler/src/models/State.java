package models;

import java.util.HashMap;

public class State {
    private String stateName;
    private boolean isFinalState;
    private HashMap<Lexeme, StateTransition> stateTransitionHashMap;
    public State(String stateName, boolean isFinalState) {
        this.stateName = stateName;
        this.isFinalState = isFinalState;
        stateTransitionHashMap = new HashMap<>();
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public boolean isFinalState() {
        return isFinalState;
    }

    public void setFinalState(boolean finalState) {
        isFinalState = finalState;
    }

    public HashMap<Lexeme, StateTransition> getStateTransitionHashMap() {
        return stateTransitionHashMap;
    }

    public void setStateTransitionHashMap(HashMap<Lexeme, StateTransition> stateTransitionHashMap) {
        this.stateTransitionHashMap = stateTransitionHashMap;
    }
    public void addStateTransition(Lexeme lexeme, String transitionState, TokenType tokenType) {
        StateTransition stateTransition = new StateTransition(transitionState,tokenType);
        stateTransitionHashMap.put(lexeme, stateTransition);
    }
    public StateTransition getStateTransition(Lexeme lexeme){
       return stateTransitionHashMap.get(lexeme);
    }
}
