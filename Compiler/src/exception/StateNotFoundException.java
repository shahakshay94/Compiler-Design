package exception;

public class StateNotFoundException extends Exception {

    private String requestedState;
    public StateNotFoundException(String requestedState) {
        super(requestedState);
        this.requestedState = requestedState;
    }

    @Override
    public String getMessage() {
        return "State not found exception--"+requestedState;
    }

    @Override
    public String toString() {
        return "State not found exception--"+requestedState;
    }
}
