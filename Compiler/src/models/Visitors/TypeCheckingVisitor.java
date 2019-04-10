package models.Visitors;

import models.AST.*;
import models.SymbolTable.SymTab;
import models.SymbolTable.SymTabEntry;
import models.Terminal;
import utils.ASTManager;
import utils.LexicalResponseManager;

/**
 * Visitor to compute the type of subexpressions and assignment statements.
 * <p>
 * This applies only to nodes that are part of expressions and assignment statements i.e.
 * AddOpNode, MultOpNode, and AssignStatNode.
 */

public class TypeCheckingVisitor extends Visitor {

    public void visit(AddOpNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        System.out.println("Visiting AddOppNode");
        String leftOperandType = node.getChildren().get(0).getType();
        String rightOperandType = node.getChildren().get(1).getType();
        if (leftOperandType.equals(rightOperandType))
            node.setType(leftOperandType);
        else {
            node.setType("typeerror");
            node.generatePosition();
            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "TYPE ERROR", "TYPE ERROR DETECTED between " + node.getChildren().get(0).getData() + " and " + node.getChildren().get(1).getData());
        }
    }

    public void visit(FactorNode factorNode) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : factorNode.getChildren())
            child.accept(this);

        if (factorNode.getChildren().size() > 0) {
            factorNode.setType(factorNode.getChildren().get(0).getType());
            factorNode.setData(factorNode.getChildren().get(0).getData());
            factorNode.m_moonVarName = (factorNode.getChildren().get(0).m_moonVarName);
        }
    }

    public void visit(TermNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.getChildren().size() > 0) {
            node.setType(node.getChildren().get(0).getType());
            node.setData(node.getChildren().get(0).getData());
            node.m_moonVarName = (node.getChildren().get(0).m_moonVarName);
        }
    }


    public void visit(ExprNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.getChildren().size() > 0) {
            node.setType(node.getChildren().get(0).getType());
            node.setData(node.getChildren().get(0).getData());
            node.m_moonVarName = (node.getChildren().get(0).m_moonVarName);
        }
    }


    public void visit(ArithExprNode node) {
        System.out.println("Visiting ArithExprNode");
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


        // only term in arithexpr node ///
        if (node.getChildren().size() == 1) {
            node.setType(node.getChildren().get(0).getType());
            node.setData(node.getChildren().get(0).getData());
            node.m_moonVarName = (node.getChildren().get(0).m_moonVarName);
            return;
        }

        // multop as child ..
        String leftOperandType = node.getChildren().get(0).getType();
        String rightOperandType = node.getChildren().get(1).getType();
        if (leftOperandType.equals(rightOperandType))
            node.setType(leftOperandType);
        else {
            node.setType("typeerror");
            node.generatePosition();
            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "TYPE ERROR", "TYPE ERROR DETECTED between " + node.getChildren().get(0).getData() + " and " + node.getChildren().get(1).getData());
        }
    }

    public void visit(MultOpNode node) {
        System.out.println("Visiting MultOpNode");
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        String leftOperandType = node.getChildren().get(0).getType();
        String rightOperandType = node.getChildren().get(1).getType();
        if (leftOperandType.equals(rightOperandType))
            node.setType(leftOperandType);
        else {
            node.setType("typeerror");
            node.generatePosition();
            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "TYPE ERROR", "TYPE ERROR DETECTED between " + node.getChildren().get(0).getData() + " and " + node.getChildren().get(1).getData());
        }
    }


    public void visit(RelExprNode node) {
        System.out.println("Visiting RelExprNode");
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        String leftOperandType = node.getChildren().get(0).getType();
        String rightOperandType = node.getChildren().get(1).getType();
        if (leftOperandType.equals(rightOperandType))
            node.setType(leftOperandType);
        else {
            node.setType("typeerror");
            node.generatePosition();
            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "TYPE ERROR", "TYPE ERROR DETECTED between " + node.getChildren().get(0).getData() + " and " + node.getChildren().get(1).getData());
        }
    }


    public void visit(AssignStatNode node) {
        System.out.println("Visiting AssignStatNode");
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        String leftOperandType = node.getChildren().get(0).getType();
        String rightOperandType = node.getChildren().get(1).getType();
        if (leftOperandType != null && leftOperandType.equals(rightOperandType))
            node.setType(leftOperandType);
        else {
            node.setType("typeerror");
            node.generatePosition();
            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "TYPE ERROR", "TYPE ERROR DETECTED between " + node.getChildren().get(0).getData() + " and " + node.getChildren().get(1).getData());
        }
    }

    public void visit(VarNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        Node currentNode = node;
        SymTabEntry currentSymTable = null;
        String nodeType = null;
        String nodeData = "";
        for (int i = 0; i < node.getChildren().size(); i++) {
            Node child = node.getChildren().get(i);
            String type = child.getType();
            String typeArray[];
            String varName;
            switch (type.split("#")[0]) {
                case "DATA":
                    typeArray = type.split("#");
                    varName = typeArray[1];
                    if (i != 0) {
                        nodeData += "_";
                    }
                    nodeData += varName;
                    int dimension = Integer.parseInt(typeArray[2]);
                    SymTabEntry varType = getTypeForVarFromSymbolTable(type, currentNode, currentSymTable);
                    if (varType == null) {
                        node.setType("typeerror");
                        node.generatePosition();
                        LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "Semantic", "Can not resolved symbol:" + varName);
                        return;
                    }
                    if (!varType.extraData.equals("integer") && !varType.extraData.equals("float")) {
                        currentSymTable = findSymbolTableForClass(varType.extraData);
                        if (currentSymTable == null || currentSymTable.m_subtable == null) {
                            node.setType("typeerror");
                            node.generatePosition();
                            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "Semantic", "Can not resolved symbol:" + varType.symbolName);
                            return;
                        }
                    } else {
                        if (dimension == 0 && varType.varDimensionSize > 0) {
                            nodeType = varType.extraData + ":" + varType.varDimensionSize;
                        } else if (dimension == varType.varDimensionSize) {
                            nodeType = varType.extraData;
                        } else {
                            nodeType = varType.extraData;
                            node.generatePosition();
                            LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "Semantic", "Invalid dimension size!!");
                        }
                    }
                    break;
                case "FCALL":
                    typeArray = type.split("#");
                    varName = typeArray[1];
                    String fParams = "";
                    if (i != 0) {
                        nodeData += "_";
                    }
                    nodeData += varName;
                    if (typeArray.length == 3) {
                        fParams = (typeArray[2]);
                    }
                    if (i != 0 && currentSymTable == null) {
                        node.setType("typeerror");
                        String errorParams = fParams.replaceAll(":", ":dim:").replaceAll("_", ",");
                        node.generatePosition();
                        LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "Semantic", "Can not resolved symbol:" + varName + "(" + errorParams + ")");
                        return;
                    }

                    SymTabEntry funcType = getTypeForFuncCall(type, currentSymTable);
                    if (funcType != null) {
                        nodeType = funcType.returnType;
                        // Set RETVAL symtab entry type
                        if (child.getChildren().size() > 0 && child.getChildren().get(0) instanceof FCallNode) {
                            child.getChildren().get(0).symtabentry.m_type = funcType.m_type;
                        }
                    } else {
                        node.setType("typeerror");
                        node.generatePosition();
                        String errorParams = fParams.replaceAll(":", ":dim:").replaceAll("_", ",");
                        LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "Semantic", "Undefined func:" + varName + "(" + errorParams + ")");
                    }
                    break;
            }
        }
        if (nodeType != null) {
            node.setType(nodeType);
        }
        node.setData(nodeData);
    }

    public SymTabEntry findSymbolTableForClass(String name) {
        Node progNode = ASTManager.getInstance().getProgNode();
        for (SymTabEntry symTabEntry : progNode.symtab.m_symlist) {
            if (symTabEntry.symbolType == SymTabEntry.SymbolType.CLASS && symTabEntry.m_subtable.m_name.equals(name)) {
                return symTabEntry;
            }
        }
        return null;
    }

    public SymTabEntry getTypeForFuncCall(String type, SymTabEntry currentSymTabEntry) {
        SymTab symTab = null;
        if (currentSymTabEntry != null) {
            symTab = currentSymTabEntry.m_subtable;
        }
        String[] typeArray = type.split("#");
        String varName = typeArray[1];
        String fParams = "";
        if (typeArray.length == 3) {
            fParams = (typeArray[2]);
        }
        if (symTab == null) {
            symTab = ASTManager.getInstance().getProgNode().symtab;
        }
        for (SymTabEntry symTabEntry : symTab.m_symlist) {
            if (symTabEntry.symbolType == SymTabEntry.SymbolType.FUNCTION && symTabEntry.symbolName.equals(varName)) {
                if ((fParams.equals(symTabEntry.extraData)) || (symTabEntry.extraData == null && fParams.isEmpty())) {
                    return symTabEntry;
                }
            }
        }

        if (currentSymTabEntry != null && currentSymTabEntry.multiLevelInheritedSymTab != null) {
            for (SymTabEntry inheritedSymTab : currentSymTabEntry.multiLevelInheritedSymTab) {
                for (SymTabEntry symTabEntry : inheritedSymTab.m_subtable.m_symlist) {
                    if (symTabEntry.symbolType == SymTabEntry.SymbolType.FUNCTION && symTabEntry.symbolName.equals(varName)) {
                        if ((fParams.equals(symTabEntry.extraData)) || (symTabEntry.extraData == null && fParams.isEmpty())) {
                            return symTabEntry;
                        }
                    }
                }
            }
        }

        return null;
    }

    public SymTabEntry getTypeForVarFromSymbolTable(String type, Node currentNode, SymTabEntry currentSymTabEntry) {
        SymTab symTab = null;
        if (currentSymTabEntry != null) {
            symTab = currentSymTabEntry.m_subtable;
        }
        String typeArray[] = type.split("#");
        String varName = typeArray[1];
        int dimension = Integer.parseInt(typeArray[2]);
        Node searchNode = currentNode;

        if (symTab != null) {
            for (SymTabEntry symTabEntry : symTab.m_symlist) {
                if (symTabEntry.symbolType == SymTabEntry.SymbolType.VARIABLE || symTabEntry.symbolType == SymTabEntry.SymbolType.PARAMETER) {
                    if (symTabEntry.symbolName.equals(varName)) {
                        return symTabEntry;
                    }
                }
            }

            for (SymTabEntry inheritedSymTab : currentSymTabEntry.multiLevelInheritedSymTab) {
                for (SymTabEntry symTabEntry : inheritedSymTab.m_subtable.m_symlist) {
                    if (symTabEntry.symbolType == SymTabEntry.SymbolType.VARIABLE || symTabEntry.symbolType == SymTabEntry.SymbolType.PARAMETER) {
                        if (symTabEntry.symbolName.equals(varName)) {
                            return symTabEntry;
                        }
                    }
                }
            }

            return null;
        }


        while (searchNode.getParent() != null) {
            if (searchNode.symtab != null) {
                for (SymTabEntry symTabEntry : searchNode.symtab.m_symlist) {
                    if (symTabEntry.symbolType == SymTabEntry.SymbolType.VARIABLE || symTabEntry.symbolType == SymTabEntry.SymbolType.PARAMETER) {
                        if (symTabEntry.symbolName.equals(varName)) {
                            return symTabEntry;
                        }
                    }
                }
            }
            searchNode = searchNode.getParent();
        }

        return null;
    }

    public void visit(VarElementNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        node.setType(node.getChildren().get(0).getType());
        node.setData(node.getChildren().get(0).getData());
    }


    public void visit(IndexListNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        node.setType("" + node.getChildren().size());
        for (Node child : node.getChildren()) {
            if (!child.getType().equals(Terminal.INT.getData())) {
                node.generatePosition();
                LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "SemanticError", "Incompatible types: Required Int");
            }
        }
    }


    public void visit(AParamsNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        String type = "";
        for (int i = 0; i < node.getChildren().size(); i++) {
            type += node.getChildren().get(i).getType() + "_";
        }
        node.setType(type);
    }

    public void visit(DataMemberNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.getChildren().size() > 1) {
            node.setType("DATA#" + node.getChildren().get(0).getData() + "#" + node.getChildren().get(1).getType());
            node.setData(node.getChildren().get(0).getData());
        }
    }


    public void visit(FCallNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.getChildren().size() > 1) {
            node.setType("FCALL#" + node.getChildren().get(0).getData() + "#" + node.getChildren().get(1).getType());
            node.setData(node.getChildren().get(0).getData());
        }
    }


    @Override
    public void visit(DimListNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ForStatNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FParamListNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FParamNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FuncDeclNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FuncDefListNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FuncDefNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ReadStatNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(IdNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(IfStatNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(InherListNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(MemberListNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FactorSignNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(TypeNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(VarDeclNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(Node node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(NumNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(OpNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ParamListNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(MainNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(WriteStatNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ReturnStatNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ScopeSpecNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(StatBlockNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ProgramBlockNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(StatementNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ClassListNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ClassNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

}
