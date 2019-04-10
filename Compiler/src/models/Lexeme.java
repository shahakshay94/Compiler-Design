package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Lexeme {

    SEMICOLON (";"),
    COLON (":"),
    COMMA (","),
    DOT ("\\."),

    OPEN_BRACKET ("\\{"),
    CLOSE_BRACKET ("\\}"),
    OPEN_SQUARE_BRACKET ("\\["),
    CLOSE_SQUARE_BRACKET ("\\]"),
    OPEN_PARENTHESIS ("\\("),
    CLOSE_PARENTHESIS ("\\)"),

    ATTRIBUTION ("="),
    GREATER (">"),
    LESSER ("<"),

    ADDITION ("\\+"),
    SUBTRACTION ("-"),
    MULTIPLICATION ("\\*"),
    DIVISION ("/"),

    ZERO ("0"),
    EXPONENTIAL ("E|e"),
    DIGIT ("[0-9]"),
    NON_ZERO ("[1-9]"),
    LETTER("[a-zA-Z]"),
    UNDERSCORE("_");

    private final Pattern pattern;

    Lexeme(String regex) {
        pattern = Pattern.compile("^" + regex);
    }

    public boolean isFind(char c) {
        Matcher m = pattern.matcher(String.valueOf(c));
        if (m.find()) {
            //System.out.println("Matched:"+m+"Position:"+m.start(0)+"returning offset:"+m.end());
            return true;
        }
        return false;
    }
}