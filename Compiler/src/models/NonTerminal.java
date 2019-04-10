package models;

public enum NonTerminal {

    PROG(1, "prog"),
    CD(2, "cd"),
    FD(3, "fd"),
    CLASSDECL(4, "classDecl"),
    OID(5, "oid"),
    MID(6, "mid"),
    VARFUNDECL(7, "varfundecl"),
    VARFUNTAIL(8, "varFunTail"),
    FUNCDECLREP(9, "funcDeclRep"),
    AR(10, "ar"),
    TYPE(11, "type"),
    ARRAYSIZE(12, "arraySize"),
    IDSRID(13, "idsrid"),
    FUNCBODY(14, "funcBody"),
    VDST(15, "VDST"),
    VDSTPRIME(16, "VDSTPrime"),
    STATEMENTPRIME(17, "statementPrime"),
    ST(18, "ST"),
    FUNCDEF(19, "funcDef"),
    FUNCHEAD(20, "funcHead"),
    FUNCDECL(21, "funcDecl"),
    FPARAMS(22, "fParams"),
    FPTAIL(23, "fpTail"),
    FPARAMSTAIL(24, "fParamsTail"),
    STATEMENT(25, "statement"),
    FORBLOCK(26, "forBlock"),
    ASSIGNSTAT(27, "assignStat"),
    STATBLOCK(28, "statBlock"),
    EXPR(29, "expr"),
    EXPRTAIL(30, "exprTail"),
    RELEXPR(31, "relExpr"),
    ARITHEXPR(32, "arithExpr"),
    ARITHEXPRTAIL(33, "arithExprTAIL"),
    TERM(34, "term"),
    TERMTAIL(35, "termTail"),
    FACTOR(36, "factor"),
    FACTORTEMP(37, "factorTemp"),
    FACTORPRIME(38, "factorPrime"),
    FACTORTEMPA(39, "factorTempA"),
    INDICEREP(40, "indiceRep"),
    INDICE(41, "indice"),
    VARIABLE(42, "variable"),
    VARIABLETAIL(43, "variableTail"),
    VARIABLEPRIME(44, "variablePrime"),
    APARAMS(45, "aParams"),
    APARAMSTAILREP(46, "aParamsTailRep"),
    APARAMSTAIL(47, "aParamsTail"),
    ADDOP(48, "addOp"),
    RELOP(49, "relOp"),
    SIGN(50, "sign"),
    MULTOP(51, "multOp"),
    ASSIGNOP(52, "assignOp");


    private final int index;
    private final String data;

    public int getIndex() {
        return index;
    }

    public String getData() {
        return data;
    }

    NonTerminal(int index, String s) {
        this.index = index;
        this.data = s;
    }

    public static int getNonTerminalIndexFromString(String input) {
        for (NonTerminal terminal : NonTerminal.values()) {
            if (terminal.getData().equals(input)) {
                return terminal.getIndex();
            }
        }
        return -1;
    }
}
