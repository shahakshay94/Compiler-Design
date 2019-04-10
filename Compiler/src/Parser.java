import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import models.GrammarExpression;
import models.LL1ParseTable;
import models.NonTerminal;
import models.Terminal;
import models.Token;
import models.TokenType;
import utils.ASTManager;
import utils.GrammarExpressionGenerator;
import utils.LexicalResponseManager;

public class Parser {
    private static Parser ourInstance = new Parser();
    private Stack<String> stack;
    private List<GrammarExpression> grammarExpressionList;
    private static final int SCAN_ERROR_CODE = 109;
    private static final int POP_ERROR_CODE = 108;
    private boolean isTokenStringCompleted = false;
    private Token token;
    private List<String> derivation = new ArrayList<>();
    private int lastIndexPushed=0;

    public static Parser getInstance() {
        return ourInstance;
    }

    private Parser() {
        initializeParser();
    }

    private void initializeParser() {
        stack = new Stack<>();
        grammarExpressionList = GrammarExpressionGenerator.generateGrammarExpression();
        LL1ParseTable.initLL1ParseTable();
    }

    public boolean parse() {
        stack.push("$");
        stack.push(grammarExpressionList.get(0).getName());
        derivation.add(stack.peek());
        LexicalResponseManager.getInstance().addDerivationToFIle(derivation);
        token = nextToken();
        if (token == null) {
            return false;
        }
        boolean success = true;
        while (!stack.peek().equals("$") && token != null) {
            String x = stack.peek();
            if (isSemanticAction(x)) {
                ASTManager.getInstance().takeSemanticAction(x);
                stack.pop();
                removeDerivation(x);
            } else if (isTerminal(x)) {
                String tokenString;
                if (token.getTokenType().getTokenType().equals(TokenType.ID.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.INTEGER.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.FLOAT.getTokenType())) {
                    tokenString = (token.getTokenType().getTokenType());
                } else {
                    tokenString = token.getTokenValue();
                }
                ASTManager.getInstance().makeNode(tokenString, token);

                if (x.equals(tokenString)) {
                    addDerivation(x,token.getTokenValue());
                    stack.pop();
//                    System.out.println(stack.toString());
                    token = nextToken();
                    if (token == null) {
                        return false;
                    }
                } else {
                    skipMissingError(x);
                    success = false;
                }
            } else {
                GrammarExpression grammarExpression = getGrammarFromParseTable(x);
                if (grammarExpression != null) {
                    stack.pop();
                    inverseRHSPush(grammarExpression);
                    addDerivation(x, grammarExpression.getProudctionList());
                } else {
                    skipError(x);
                    success = false;
                }
            }
        }
        LexicalResponseManager.getInstance().addDerivationToFIle(derivation);
        if(token==null){
            return success;
        }
        return (!token.getTokenType().getTokenType().equals("$")) && (success);
    }

    private void addDerivation(String x, List<String> proudctionList) {
        int indexToPush = -1;
        for (int index = 0; index < derivation.size(); index++) {
            String der = derivation.get(index);
            if (der.equals(x) && lastIndexPushed<=index) {
                indexToPush = index;
                break;
            }
        }
        for (Iterator<String> iterator = proudctionList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("EPSILON")) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }
        derivation.remove(indexToPush);
        lastIndexPushed=indexToPush;
        derivation.addAll(indexToPush,proudctionList);
        LexicalResponseManager.getInstance().addDerivationToFIle(derivation);
    }
    private void addDerivation(String x, String terminal) {
        int indexToPush = -1;
        for (int index = 0; index < derivation.size(); index++) {
            String der = derivation.get(index);
            if (der.equals(x) && lastIndexPushed<=index) {
                indexToPush = index;
                break;
            }
        }
        derivation.remove(indexToPush);
        lastIndexPushed=indexToPush;
        derivation.add(indexToPush,terminal);
        LexicalResponseManager.getInstance().addDerivationToFIle(derivation);
    }
    private void removeDerivation(String x) {
        int indexToPush = -1;
        for (int index = 0; index < derivation.size(); index++) {
            String der = derivation.get(index);
            if (der.equals(x)) {
                indexToPush = index;
                break;
            }
        }
        derivation.remove(indexToPush);
    }


    private boolean isSemanticAction(String x) {
        return (x.contains("SEMANTIC"));
    }


    private void inverseRHSPush(GrammarExpression grammarExpression) {
        if (grammarExpression != null) {
            for (int index = grammarExpression.getProudctionList().size() - 1; index >= 0; index--) {
                String production = grammarExpression.getProudctionList().get(index);
                if (!production.equals("EPSILON")) {
                    stack.push(production);
                }
            }
//            System.out.println(stack.toString());
        }
    }

    private GrammarExpression getGrammarFromParseTable(String x) {
        String tokenString;
        if (token.getTokenType().getTokenType().equals(TokenType.ID.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.INTEGER.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.FLOAT.getTokenType())) {
            tokenString = (token.getTokenType().getTokenType());
        } else {
            tokenString = token.getTokenValue();
        }
        int terminalIndex = Terminal.getTerminalIndexFromString(tokenString);
        int nonTerminalIndex = NonTerminal.getNonTerminalIndexFromString(x);
        if (terminalIndex != -1 && nonTerminalIndex != -1) {
            int LL1ParseTableEntry = LL1ParseTable.Table[nonTerminalIndex][terminalIndex];
//            System.out.println("Grammar for top:" + x + "  token:" + tokenString + " terminalIndex:" + terminalIndex + " nonTerminalIndex:" + nonTerminalIndex + " LLEntry:" + LL1ParseTableEntry);
            if (LL1ParseTableEntry != POP_ERROR_CODE && LL1ParseTableEntry != SCAN_ERROR_CODE) {
                GrammarExpression grammarExpression = grammarExpressionList.get(LL1ParseTableEntry - 1);
//                System.out.println("Grammar:" + grammarExpression.getProudctionList().toString());
                return grammarExpression;
            }
        }
        return null;
    }

    private void skipMissingError(String input){
        LexicalResponseManager.getInstance().writeSyntacticalMissingError(input,token);
        stack.pop();
    }

    private void skipError(String input) {
        System.out.println("Syntax error at:" + token.getLineNumber());
        LexicalResponseManager.getInstance().writeSyntacticalError(token);
        int LL1ParseTableEntry = getParseTableEntry(input);
        if (LL1ParseTableEntry == POP_ERROR_CODE || LL1ParseTableEntry==-1) {
            stack.pop();
        } else {
            while (LL1ParseTableEntry == SCAN_ERROR_CODE) {
                token = nextToken();
                if (token == null) {
                    return;
                }
                LL1ParseTableEntry = getParseTableEntry(input);
            }
        }
    }

    private int getParseTableEntry(String input) {
        String tokenString;
        if (token.getTokenType().getTokenType().equals(TokenType.ID.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.INTEGER.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.FLOAT.getTokenType())) {
            tokenString = (token.getTokenType().getTokenType());
        } else {
            tokenString = token.getTokenValue();
        }
        int terminalIndex = Terminal.getTerminalIndexFromString(tokenString);
        int nonTerminalIndex = NonTerminal.getNonTerminalIndexFromString(input);
        if (terminalIndex != -1 && nonTerminalIndex != -1) {
            int LL1ParseTableEntry = LL1ParseTable.Table[nonTerminalIndex][terminalIndex];
            return LL1ParseTableEntry;
        }
        return -1;
    }

    private Token nextToken() {
        Token token = TokenGenerator.getInstance().getNextToken();
        LexicalResponseManager.getInstance().writeLexicalResponseToFile(token);
        if (token == null) {
            if (!isTokenStringCompleted) {
                token = new Token(TokenType.RESERVED, "$");
                isTokenStringCompleted = true;
                return token;
            } else {
                return null;
            }
        }
        while (token.getTokenType()==(TokenType.INVALID_IDENTIFIER) || token.getTokenType()==(TokenType.INVALID_NUMBER)){
            token = TokenGenerator.getInstance().getNextToken();
            if(token==null){
                break;
            }
            LexicalResponseManager.getInstance().writeLexicalResponseToFile(token);
        }

        return token;
    }


    private boolean isTerminal(String input) {
        int terminalIndex = Terminal.getTerminalIndexFromString(input);
        return terminalIndex != -1;
    }
}
