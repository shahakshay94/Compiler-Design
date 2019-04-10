package utils;

import models.Lexeme;
import models.StateTransitionTable;
import models.TokenType;

public class TableParserBuilder {
    private static TableParserBuilder ourInstance = new TableParserBuilder();

    public static TableParserBuilder getInstance() {
        return ourInstance;
    }

    private TableParserBuilder() {
    }

    public void buildParser(){

        // State transition for state Q0 ::::
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.COMMA,"Q1", TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.SUBTRACTION,"Q1",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.ADDITION,"Q1",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.DOT,"Q1",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.DIVISION,"Q5",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.ZERO,"Q8",TokenType.INTEGER,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.COLON,"Q15",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.SEMICOLON,"Q1",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.LESSER,"Q13",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.ATTRIBUTION,"Q7",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.GREATER,"Q7",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.OPEN_BRACKET,"Q1",TokenType.PUNCTUATION,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.CLOSE_BRACKET,"Q1",TokenType.PUNCTUATION,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.OPEN_SQUARE_BRACKET,"Q1",TokenType.PUNCTUATION,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.CLOSE_SQUARE_BRACKET,"Q1",TokenType.PUNCTUATION,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.OPEN_PARENTHESIS,"Q1",TokenType.PUNCTUATION,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.CLOSE_PARENTHESIS,"Q1",TokenType.PUNCTUATION,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.LETTER,"Q6",TokenType.ID,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.MULTIPLICATION,"Q4",TokenType.OPERATOR,false);
        StateTransitionTable.getInstance().addStateTransition("Q0", Lexeme.NON_ZERO,"Q3",TokenType.INTEGER,false);

        // State transition for state Q1 ::::
        StateTransitionTable.getInstance().addStateTransition("Q1",null,null,null,true);

        // State transition for state Q2 ::::
        StateTransitionTable.getInstance().addStateTransition("Q2", Lexeme.DIGIT,"Q2",null,false);
        StateTransitionTable.getInstance().addStateTransition("Q2", Lexeme.NON_ZERO,"Q10",TokenType.FLOAT,false);


        // State transition for state Q3 ::::
        StateTransitionTable.getInstance().addStateTransition("Q3", Lexeme.DOT,"Q9",null,true);
        StateTransitionTable.getInstance().addStateTransition("Q3", Lexeme.DIGIT,"Q3",TokenType.INTEGER,true);


        // State transition for state Q4 ::::
        StateTransitionTable.getInstance().addStateTransition("Q4", Lexeme.DIVISION,"Q1",TokenType.PUNCTUATION,true);


        // State transition for state Q5 ::::
        StateTransitionTable.getInstance().addStateTransition("Q5", Lexeme.DIVISION,"Q1",TokenType.PUNCTUATION,true);
        StateTransitionTable.getInstance().addStateTransition("Q5", Lexeme.MULTIPLICATION,"Q1",TokenType.PUNCTUATION,true);


        // State transition for state Q6 ::::
        StateTransitionTable.getInstance().addStateTransition("Q6", Lexeme.LETTER,"Q6",TokenType.ID,true);
        StateTransitionTable.getInstance().addStateTransition("Q6", Lexeme.UNDERSCORE,"Q6",TokenType.ID,true);
        StateTransitionTable.getInstance().addStateTransition("Q6", Lexeme.DIGIT,"Q6",TokenType.ID,true);


        // State transition for state Q7 ::::
        StateTransitionTable.getInstance().addStateTransition("Q7", Lexeme.ATTRIBUTION,"Q1",TokenType.OPERATOR,true);


        // State transition for state Q8 ::::
        StateTransitionTable.getInstance().addStateTransition("Q8", Lexeme.DOT,"Q9",null,true);


        // State transition for state Q9 ::::
        StateTransitionTable.getInstance().addStateTransition("Q9", Lexeme.ZERO,"Q10",TokenType.FLOAT,false);
        StateTransitionTable.getInstance().addStateTransition("Q9", Lexeme.NON_ZERO,"Q10",TokenType.FLOAT,false);
        StateTransitionTable.getInstance().addStateTransition("Q9", Lexeme.DIGIT,"Q2",null,false);


        // State transition for state Q10 ::::
        StateTransitionTable.getInstance().addStateTransition("Q10", Lexeme.EXPONENTIAL,"Q11",null,true);


        // State transition for state Q11 ::::
        StateTransitionTable.getInstance().addStateTransition("Q11", Lexeme.ZERO,"Q1",TokenType.FLOAT,false);
        StateTransitionTable.getInstance().addStateTransition("Q11", Lexeme.NON_ZERO,"Q14",TokenType.FLOAT,false);
        StateTransitionTable.getInstance().addStateTransition("Q11", Lexeme.ADDITION,"Q12",null,false);
        StateTransitionTable.getInstance().addStateTransition("Q11", Lexeme.SUBTRACTION,"Q12",null,false);


        // State transition for state Q12 ::::
        StateTransitionTable.getInstance().addStateTransition("Q12", Lexeme.ZERO,"Q1",TokenType.FLOAT,false);
        StateTransitionTable.getInstance().addStateTransition("Q12", Lexeme.NON_ZERO,"Q14",TokenType.FLOAT,false);


        // State transition for state Q13 ::::
        StateTransitionTable.getInstance().addStateTransition("Q13", Lexeme.ATTRIBUTION,"Q1",TokenType.OPERATOR,true);
        StateTransitionTable.getInstance().addStateTransition("Q13", Lexeme.GREATER,"Q1",TokenType.OPERATOR,true);

        // State transition for state Q14 ::::
        StateTransitionTable.getInstance().addStateTransition("Q14", Lexeme.DIGIT,"Q14",TokenType.FLOAT,true);

        // State transition for state Q15 ::::
        StateTransitionTable.getInstance().addStateTransition("Q15", Lexeme.COLON,"Q1",TokenType.OPERATOR,true);

    }

}
