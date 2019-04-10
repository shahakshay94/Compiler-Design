package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import models.Terminal;
import models.Token;
import models.TokenType;
import models.AST.AParamsNode;
import models.AST.ArithExprNode;
import models.AST.AssignStatNode;
import models.AST.ClassListNode;
import models.AST.ClassNode;
import models.AST.DataMemberNode;
import models.AST.DimListNode;
import models.AST.ExprNode;
import models.AST.FCallNode;
import models.AST.FParamListNode;
import models.AST.FParamNode;
import models.AST.FactorNode;
import models.AST.ForStatNode;
import models.AST.FuncDeclNode;
import models.AST.FuncDefListNode;
import models.AST.FuncDefNode;
import models.AST.IdNode;
import models.AST.IfStatNode;
import models.AST.IndexListNode;
import models.AST.InherListNode;
import models.AST.MainNode;
import models.AST.MemberListNode;
import models.AST.MultOpNode;
import models.AST.Node;
import models.AST.NumNode;
import models.AST.OpNode;
import models.AST.ProgramBlockNode;
import models.AST.ReadStatNode;
import models.AST.RelExprNode;
import models.AST.ReturnStatNode;
import models.AST.ScopeSpecNode;
import models.AST.StatBlockNode;
import models.AST.StatementNode;
import models.AST.TermNode;
import models.AST.TypeNode;
import models.AST.VarDeclNode;
import models.AST.VarElementNode;
import models.AST.VarNode;
import models.AST.WriteStatNode;

public class ASTManager {
    private static ASTManager ourInstance = new ASTManager();
    private Stack<Node> semanticStack = new Stack<>();
    private Node progNode;

    public static ASTManager getInstance() {
        return ourInstance;
    }

    private ASTManager() {
    }

    public Node getProgNode() {
        return progNode;
    }

    public void takeSemanticAction(String x) {
        try {
            switch (x) {
                case "SEMANTIC_MAKE_FAMILY_PROG": {
                    Node classList = new ClassListNode();
                    Node funcDefList = new FuncDefListNode();
                    Node progBlock = new ProgramBlockNode();
                    if (semanticStack.peek().getNodeCategory().equals("statBlock")) {
                        Node statBlock = (semanticStack.pop());
                        progBlock = new ProgramBlockNode(statBlock.getChildren());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("funcDefList")) {
                        funcDefList = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("classList")) {
                        classList = (semanticStack.pop());
                    }
                    progNode = new MainNode(classList, funcDefList, progBlock);
                    progNode.setNodeCategory("prog");
                    semanticStack.push(progNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_CLASSLIST": {
                    Node classList = new ClassListNode();
                    List<Node> childNodeList = new ArrayList<>();
                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("classDecl"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            classList.lineNumber = semanticStack.peek().lineNumber;
                            classList.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    classList.addAllChildInReverse(childNodeList);
                    classList.setNodeCategory("classList");
                    semanticStack.push(classList);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FUNCTION_LIST": {
                    Node functionListNode = new FuncDefListNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("funcDef"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            functionListNode.lineNumber = semanticStack.peek().lineNumber;
                            functionListNode.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    functionListNode.addAllChildInReverse(childNodeList);
                    functionListNode.setNodeCategory("funcDefList");
                    semanticStack.push(functionListNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_CLASS_DECL": {
                    Node memberList = new MemberListNode();
                    Node inherList = new InherListNode();
                    if (semanticStack.peek().getNodeCategory().equals("memberList")) {
                        memberList = (semanticStack.pop());
                    }

                    if (semanticStack.peek().getNodeCategory().equals("inherList")) {
                        inherList = (semanticStack.pop());
                    }

                    Node idNode = (semanticStack.pop()); //  id ....
                    Node classDecl = new ClassNode(idNode, inherList, memberList);
                    classDecl.lineNumber = idNode.lineNumber;
                    classDecl.colNumber = idNode.colNumber;

                    classDecl.setNodeCategory("classDecl");
                    semanticStack.push(classDecl);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_VAR_FUNC_DECL": {
                    Node memberList = new MemberListNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("funcDecl"))) {
                        childNodeList.add(semanticStack.pop());
                    }
                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("varDecl"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            memberList.lineNumber = semanticStack.peek().lineNumber;
                            memberList.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());

                    }
                    memberList.addAllChildInReverse(childNodeList);
                    memberList.setNodeCategory("memberList");
                    semanticStack.push(memberList);
                    break;

                }
                case "SEMANTIC_MAKE_FAMILY_INHER_LIST": {
                    Node inherList = new InherListNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("id"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            inherList.lineNumber = semanticStack.peek().lineNumber;
                            inherList.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    inherList.addAllChildInReverse(childNodeList);
                    inherList.setNodeCategory("inherList");
                    semanticStack.push(inherList);
                    break;
                }
                case "SEMANTIC_MAKE_NODE_CLASS_ID": {
                    semanticStack.peek().setNodeCategory("classId");
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_SR_ID": {
                    Node idNode = semanticStack.pop();
                    Node scopeSpec = new ScopeSpecNode(idNode.getData());
                    scopeSpec.setNodeCategory("scopeSpec");
                    scopeSpec.lineNumber = idNode.lineNumber;
                    scopeSpec.colNumber = idNode.colNumber;
                    semanticStack.push(scopeSpec);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_DIM_LIST": {
                    Node dimListNode = new DimListNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("num"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            dimListNode.lineNumber = semanticStack.peek().lineNumber;
                            dimListNode.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    dimListNode.addAllChildInReverse(childNodeList);
                    dimListNode.setNodeCategory("dimList");
                    semanticStack.push(dimListNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FPARAM_LIST": {
                    Node fParamListNode = new FParamListNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("fparam"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            fParamListNode.lineNumber = semanticStack.peek().lineNumber;
                            fParamListNode.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    fParamListNode.addAllChildInReverse(childNodeList);
                    fParamListNode.setNodeCategory("fparmList");
                    semanticStack.push(fParamListNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_INDEX_LIST": {
                    Node indexListNode = new IndexListNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("arithExpr"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            indexListNode.lineNumber = semanticStack.peek().lineNumber;
                            indexListNode.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    indexListNode.addAllChildInReverse(childNodeList);
                    indexListNode.setNodeCategory("indexList");
                    if (indexListNode.getChildren().size() > 0) {
                        semanticStack.push(indexListNode);
                    }
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_A_PARAMS": {
                    Node aParams = new AParamsNode();
                    List<Node> childNodeList = new ArrayList<>();
                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("expr"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            aParams.lineNumber = semanticStack.peek().lineNumber;
                            aParams.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    aParams.addAllChildInReverse(childNodeList);
                    aParams.setNodeCategory("aParams");
                    semanticStack.push(aParams);
                    break;
                }
                /*case "SEMANTIC_MAKE_FAMILY_INDICE": {
                    //TODO inice is left we dont have any AST for indice ..
                    error part left
                    transferFromOneFamilyToOther(semanticStack.peek(), "indice");
                    break;
                }*/
                case "SEMANTIC_MAKE_FAMILY_FACTOR": {
                    Node factorVarFCallNode = semanticStack.pop();
                    Node factorNode = new FactorNode(factorVarFCallNode);
                    factorNode.setNodeCategory("factor");
                    factorNode.lineNumber = factorVarFCallNode.lineNumber;
                    factorNode.colNumber = factorVarFCallNode.colNumber;
                    semanticStack.push(factorNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FACTOR_NUM": {
                    Node numNode = semanticStack.pop();
                    Node factorNumNode = new FactorNode(numNode);
                    factorNumNode.setNodeCategory("factor");
                    factorNumNode.lineNumber = numNode.lineNumber;
                    factorNumNode.colNumber = numNode.colNumber;
                    semanticStack.push(factorNumNode);
                    break;
                }

                case "SEMANTIC_MAKE_FAMILY_FACTOR_ARITH_EXPR": {
                    Node arithExprNode = (semanticStack.pop());
                    Node factorNotNode = new FactorNode(arithExprNode);
                    factorNotNode.setNodeCategory("factor");
                    factorNotNode.lineNumber = arithExprNode.lineNumber;
                    factorNotNode.colNumber = arithExprNode.colNumber;
                    semanticStack.push(factorNotNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FACTOR_NOT": {
                    Node factor = (semanticStack.pop());
                    Node factorNotNode = new FactorNode("!", factor);
                    factorNotNode.setNodeCategory("factor");
                    factorNotNode.lineNumber = factor.lineNumber;
                    factorNotNode.colNumber = factor.colNumber;
                    semanticStack.push(factorNotNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FACTOR_SIGN": {
                    Node factor = (semanticStack.pop());
                    Node signNode = semanticStack.pop();
                    Node factorSignNode = new FactorNode(signNode.getData(), factor);
                    factorSignNode.setNodeCategory("factor");
                    factorSignNode.lineNumber = signNode.lineNumber;
                    factorSignNode.colNumber = signNode.colNumber;

                    semanticStack.push(factorSignNode);

                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_VAR_DECL": {
                    Node dimListNode = new DimListNode();
                    if (semanticStack.peek().getNodeCategory().equals("dimList")) {
                        dimListNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop()); // IdNode
                    Node typeNode = (semanticStack.pop());
                    Node varDeclNode = new VarDeclNode(typeNode, idNode, dimListNode);
                    varDeclNode.lineNumber = idNode.lineNumber;
                    varDeclNode.colNumber = idNode.colNumber;
                    varDeclNode.setNodeCategory("varDecl");
                    semanticStack.push(varDeclNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FUNC_DECL": {
                    Node fParamListNode = new FParamListNode();

                    if (semanticStack.peek().getNodeCategory().equals("fparmList")) {
                        fParamListNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop()); // IdNode
                    Node typeNode = (semanticStack.pop());
                    Node funcDeclNode = new FuncDeclNode(typeNode, idNode, fParamListNode);
                    funcDeclNode.lineNumber = idNode.lineNumber;
                    funcDeclNode.colNumber = idNode.colNumber;
                    funcDeclNode.setNodeCategory("funcDecl");
                    semanticStack.push(funcDeclNode);
                    break;
                }

                case "SEMANTIC_MAKE_FAMILY_FPARAM": {
                    Node dimListNode = new DimListNode();
                    if (semanticStack.peek().getNodeCategory().equals("dimList")) {
                        dimListNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop()); // IdNode
                    Node typeNode = (semanticStack.pop());
                    Node fparamNode = new FParamNode(typeNode, idNode, dimListNode);
                    fparamNode.lineNumber = idNode.lineNumber;
                    fparamNode.colNumber = idNode.colNumber;
                    fparamNode.setNodeCategory("fparam");
                    semanticStack.push(fparamNode);
                    break;
                }

                case "SEMANTIC_MAKE_FAMILY_EXPR": {
                    //TODO factor should not be come here it should automatically set as arithexpr or relexpr
                    Node expNode = new ExprNode();
                    if (semanticStack.peek().getNodeCategory().equals("arithExpr") || semanticStack.peek().getNodeCategory().equals("relExpr") || (semanticStack.peek().getNodeCategory().equals("factor"))) {
                        expNode.lineNumber = semanticStack.peek().lineNumber;
                        expNode.colNumber = semanticStack.peek().colNumber;
                        expNode.addChild(semanticStack.pop());
                    }
                    expNode.setNodeCategory("expr");
                    semanticStack.push(expNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FUNC_DEF": {
                    Node scopSpecNode = new ScopeSpecNode("");
                    Node fParamListNode = new FParamListNode();
                    Node statBlockNode = new StatBlockNode();

                    if (semanticStack.peek().getNodeCategory().equals("statBlock")) {
                        statBlockNode = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("fparmList")) {
                        fParamListNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop()); // ID
                    if (semanticStack.peek().getNodeCategory().equals("scopeSpec")) {
                        scopSpecNode = (semanticStack.pop());
                    }
                    Node typeNode = (semanticStack.pop()); // Type

                    Node funcDefNode = new FuncDefNode(typeNode, scopSpecNode, idNode, fParamListNode, statBlockNode);
                    funcDefNode.lineNumber = idNode.lineNumber;
                    funcDefNode.colNumber = idNode.colNumber;
                    funcDefNode.setNodeCategory("funcDef");
                    semanticStack.push(funcDefNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_VAR": {
                    Node varNode = new VarNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("varElement"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            varNode.lineNumber = semanticStack.peek().lineNumber;
                            varNode.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    varNode.addAllChildInReverse(childNodeList);
                    varNode.setNodeCategory("var");
                    semanticStack.push(varNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_VAR_ELEMENT": {
                    Node varElementNode = new VarElementNode();
                    if (semanticStack.peek().getNodeCategory().equals("dataMember") || semanticStack.peek().getNodeCategory().equals("fCall")) {
                        varElementNode.lineNumber = semanticStack.peek().lineNumber;
                        varElementNode.colNumber = semanticStack.peek().colNumber;
                        varElementNode.addChild(semanticStack.pop());
                    }
                    varElementNode.setNodeCategory("varElement");
                    semanticStack.push(varElementNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_TERM": {
                    Node multOpOrFactorNode = semanticStack.pop();
                    Node termNode = new TermNode(multOpOrFactorNode);
                    termNode.setNodeCategory("term");
                    termNode.lineNumber = multOpOrFactorNode.lineNumber;
                    termNode.colNumber = multOpOrFactorNode.colNumber;
                    semanticStack.push(termNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_MULT_OP": {
                    Node rightChild = semanticStack.pop();
                    Node opNode = semanticStack.pop();
                    Node leftChild = semanticStack.pop();
                    Node multOpNode = new MultOpNode(opNode.getData(), leftChild, rightChild);
                    multOpNode.setNodeCategory("multOp");
                    multOpNode.lineNumber = opNode.lineNumber;
                    multOpNode.colNumber = opNode.colNumber;
                    semanticStack.push(multOpNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_ARITH_EXPR": {
                    Node rightChild = semanticStack.pop();
                    Node opNode = semanticStack.pop();
                    Node leftChild = semanticStack.pop();
                    Node arithExprNode = new ArithExprNode(opNode.getData(), leftChild, rightChild);
                    arithExprNode.setNodeCategory("arithExpr");
                    arithExprNode.lineNumber = opNode.lineNumber;
                    arithExprNode.colNumber = opNode.colNumber;
                    semanticStack.push(arithExprNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_ARITH_EXPR_FINAL": {
                    if (semanticStack.peek().getNodeCategory().equals("term")) {
                        Node termNode = semanticStack.pop();
                        Node arithExprNode = new ArithExprNode();
                        arithExprNode.addChild(termNode);
                        arithExprNode.setNodeCategory("arithExpr");
                        arithExprNode.lineNumber = termNode.lineNumber;
                        arithExprNode.colNumber = termNode.colNumber;
                        semanticStack.push(arithExprNode);
                    }
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_REL_EXPR": {
                    Node rightChild = semanticStack.pop();
                    Node opNode = semanticStack.pop();
                    Node leftChild = semanticStack.pop();
                    Node relExprNode = new RelExprNode(opNode.getData(), leftChild, rightChild);
                    relExprNode.setNodeCategory("relExpr");
                    relExprNode.lineNumber = opNode.lineNumber;
                    relExprNode.colNumber = opNode.colNumber;
                    semanticStack.push(relExprNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_DATA_MEMBER": {
                    Node indexListNode = new IndexListNode();
                    if (semanticStack.peek().getNodeCategory().equals("indexList")) {
                        indexListNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop());
                    Node dataMemberNode = new DataMemberNode(idNode, indexListNode);
                    dataMemberNode.lineNumber = idNode.lineNumber;
                    dataMemberNode.colNumber = idNode.colNumber;
                    dataMemberNode.setNodeCategory("dataMember");
                    semanticStack.push(dataMemberNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_F_CALL": {
                    Node aparamsNode = new AParamsNode();
                    if (semanticStack.peek().getNodeCategory().equals("aParams")) {
                        aparamsNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop());
                    Node fcallNode = new FCallNode(idNode, aparamsNode);
                    fcallNode.setNodeCategory("fCall");
                    fcallNode.lineNumber = idNode.lineNumber;
                    fcallNode.colNumber = idNode.colNumber;
                    semanticStack.push(fcallNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_IF_STAT": {
                    Node ifStatBlockNode = new StatBlockNode();
                    Node elseStatBlockNode = new StatBlockNode();
                    Node exprNode = new ExprNode();
                    if (semanticStack.peek().getNodeCategory().equals("statBlock")) {
                        elseStatBlockNode = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("statBlock")) {
                        ifStatBlockNode = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("expr")) {
                        exprNode = (semanticStack.pop());
                    }
                    Node ifStatNode = new IfStatNode(exprNode, ifStatBlockNode, elseStatBlockNode);
                    ifStatNode.lineNumber = exprNode.lineNumber;
                    ifStatNode.colNumber = exprNode.colNumber;
                    ifStatNode.setNodeCategory("ifStat");
                    semanticStack.push(ifStatNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_FOR_STAT": {
                    Node statBlockNode = new StatBlockNode();
                    Node assignStatNode = new AssignStatNode();
                    Node relExprNode = new RelExprNode();
                    Node exprNode = new ExprNode();
                    if (semanticStack.peek().getNodeCategory().equals("statBlock")) {
                        statBlockNode = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("assignStat")) {
                        assignStatNode = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("relExpr")) {
                        relExprNode = (semanticStack.pop());
                    }
                    if (semanticStack.peek().getNodeCategory().equals("expr")) {
                        exprNode = (semanticStack.pop());
                    }
                    Node idNode = (semanticStack.pop());
                    Node typeNode = (semanticStack.pop());
                    Node forNode = new ForStatNode(typeNode, idNode, exprNode, relExprNode, assignStatNode, statBlockNode);
                    forNode.lineNumber = idNode.lineNumber;
                    forNode.colNumber = idNode.colNumber;
                    forNode.setNodeCategory("forStat");
                    semanticStack.push(forNode);
                    break;
                }

                case "SEMANTIC_MAKE_FAMILY_GET_STAT": {
                    Node getStatNode = new ReadStatNode();
                    getStatNode.setNodeCategory("getStat");
                    getStatNode.lineNumber = semanticStack.peek().lineNumber;
                    getStatNode.colNumber = semanticStack.peek().colNumber;
                    getStatNode.addChild(semanticStack.pop());
                    semanticStack.push(getStatNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_PUT_STAT": {
                    Node putStatNode = new WriteStatNode();
                    putStatNode.setNodeCategory("putStat");
                    putStatNode.lineNumber = semanticStack.peek().lineNumber;
                    putStatNode.colNumber = semanticStack.peek().colNumber;
                    putStatNode.addChild(semanticStack.pop());
                    semanticStack.push(putStatNode);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_RETURN_STAT": {
                    Node returnStat = new ReturnStatNode();
                    returnStat.setNodeCategory("returnStat");
                    returnStat.lineNumber = semanticStack.peek().lineNumber;
                    returnStat.colNumber = semanticStack.peek().colNumber;
                    returnStat.addChild(semanticStack.pop());
                    semanticStack.push(returnStat);
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_STATEMENT": {
                    // TODO also possible solution : semanticStack.peek().setNodeCategory("statement");

                    Node statementNode = new StatementNode();
                    statementNode.lineNumber = semanticStack.peek().lineNumber;
                    statementNode.colNumber = semanticStack.peek().colNumber;
                    statementNode.setNodeCategory("statement");
                    statementNode.addChild(semanticStack.pop());
                    semanticStack.push(statementNode);
                    break;
                }

                case "SEMANTIC_MAKE_FAMILY_STAT_BODY": {
                }
                case "SEMANTIC_MAKE_FAMILY_STATEMENT_BLOCK": {
                }
                case "SEMANTIC_MAKE_FAMILY_ST_BLOCK": {
                    //TODO should not have statblock in while condition fix it...
                    Node stateBlock = new StatBlockNode();
                    List<Node> childNodeList = new ArrayList<>();

                    while (semanticStack.size() > 0 && (semanticStack.peek().getNodeCategory().equals("statement") || semanticStack.peek().getNodeCategory().equals("varDecl"))) {
                        if(semanticStack.peek().lineNumber>0) {
                            stateBlock.lineNumber = semanticStack.peek().lineNumber;
                            stateBlock.colNumber = semanticStack.peek().colNumber;
                        }
                        childNodeList.add(semanticStack.pop());
                    }
                    stateBlock.addAllChildInReverse(childNodeList);
                    stateBlock.setNodeCategory("statBlock");
                    if (stateBlock.getChildren().size() > 0) {
                        semanticStack.push(stateBlock);
                    }
                    break;
                }
                case "SEMANTIC_MAKE_FAMILY_ASSIGN_LEFT_VAR": {
                    //TODO left var is left test if it works fine or not ...
                    semanticStack.peek().setNodeCategory("assignLeftVar");
                    break;
                }

                case "SEMANTIC_MAKE_FAMILY_ASSIGN_STAT": {
                    Node rightChild = semanticStack.pop();
                    Node leftChild = semanticStack.pop();

                    Node assignStatNode = new AssignStatNode(leftChild, rightChild);
                    assignStatNode.lineNumber = leftChild.lineNumber;
                    assignStatNode.colNumber = leftChild.colNumber;
                    assignStatNode.setNodeCategory("assignStat");
                    semanticStack.push(assignStatNode);
                    break;
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }


    public void makeNode(String tokenType, Token token) {
        try {
            if (token.getTokenType().getTokenType().equals(TokenType.INTEGER.getTokenType()) || token.getTokenType().getTokenType().equals(TokenType.FLOAT.getTokenType())) {
                Node numNode = new NumNode(token.getTokenValue());
                numNode.setNodeCategory("num");
                numNode.setType(token.getTokenType().getTokenType());
                if(token.getTokenType().getTokenType().equals(TokenType.INTEGER.getTokenType())) {
                    numNode.setType(Terminal.INT.getData());
                }
                numNode.lineNumber = token.getLineNumber();
                numNode.colNumber = token.getColumnNumber();
                semanticStack.push(numNode);
            } else if (tokenType.equals(Terminal.id.getData())) {
                Node node = new IdNode(token.getTokenValue());
                node.setNodeCategory("id");
                node.lineNumber = token.getLineNumber();
                node.colNumber = token.getColumnNumber();
                semanticStack.push(node);
            } else if (tokenType.equals(Terminal.INT.getData()) || tokenType.equals(Terminal.FLOAT.getData())) {
                Node typeNode = new TypeNode(tokenType);
                typeNode.setType(tokenType);
                typeNode.setNodeCategory("type");
                typeNode.lineNumber = token.getLineNumber();
                typeNode.colNumber = token.getColumnNumber();
                semanticStack.push(typeNode);

            } else if (tokenType.equals(Terminal.EQ.getData()) || tokenType.equals(Terminal.NEQ.getData()) || tokenType.equals(Terminal.GT.getData()) || tokenType.equals(Terminal.LT.getData()) || tokenType.equals(Terminal.LEQ.getData()) || tokenType.equals(Terminal.GEQ.getData())) {
                Node relOpNode = new OpNode(tokenType);
                relOpNode.setNodeCategory("relOp");
                relOpNode.lineNumber = token.getLineNumber();
                relOpNode.colNumber = token.getColumnNumber();
                semanticStack.push(relOpNode);
            } else if (tokenType.equals(Terminal.ADD.getData()) || tokenType.equals(Terminal.MINUS.getData()) || tokenType.equals(Terminal.OR.getData()) || tokenType.equals(Terminal.MUL.getData()) || tokenType.equals(Terminal.DIV.getData()) || tokenType.equals(Terminal.AND.getData())) {
                Node multOpNode = new OpNode(tokenType);
                multOpNode.setNodeCategory("multOp");
                multOpNode.lineNumber = token.getLineNumber();
                multOpNode.colNumber = token.getColumnNumber();
                semanticStack.push(multOpNode);
            }

        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }
}
