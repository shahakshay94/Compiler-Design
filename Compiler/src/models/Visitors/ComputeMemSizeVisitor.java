package models.Visitors;

import models.AST.*;
import models.SymbolTable.SymTab;
import models.SymbolTable.SymTabEntry;
import models.Terminal;
import utils.ASTManager;

import java.io.File;
import java.io.PrintWriter;

import static models.SymbolTable.SymTabEntry.SymbolDataType.*;

public class ComputeMemSizeVisitor extends Visitor {

    public String m_outputfilename = "";

    public ComputeMemSizeVisitor() {
    }

    public ComputeMemSizeVisitor(String p_filename) {
        this.m_outputfilename = p_filename;
    }


    public int getSizeOfClassObject(String className) {
        int size = 0;
        Node progNode = ASTManager.getInstance().getProgNode();
        for (SymTabEntry symTabEntry : progNode.symtab.m_symlist) {
            if (symTabEntry.symbolType == SymTabEntry.SymbolType.CLASS && symTabEntry.m_subtable != null && symTabEntry.m_subtable.m_name.equals(className)) {
                if (symTabEntry.m_subtable.m_size > 0) {
                    return symTabEntry.m_subtable.m_size;
                }
                return calculateMemSizeOfClassNode(symTabEntry);
            }
        }
        return size;
    }

    public int calculateMemSizeOfClassNode(SymTabEntry symTabEntry) {
        int size = 0;
        for (SymTabEntry classSymTabEntry : symTabEntry.m_subtable.m_symlist) {
            if (classSymTabEntry.symbolDataType != CLASS) {
                size += sizeOfEntry(classSymTabEntry);
            } else {
                size += getSizeOfClassObject(classSymTabEntry.extraData);
            }
        }
        for (SymTabEntry inheritedSymTab : symTabEntry.multiLevelInheritedSymTab) {
            size += getSizeOfClassObject(inheritedSymTab.symbolName);
        }
        symTabEntry.m_subtable.m_size = size;
        return size;
    }


    public int sizeOfEntry(Node p_node) {
        int size = 0;
        try {
            if ((p_node.getType() != null && p_node.getType().equals(Terminal.INT.getData())) || (p_node.symtabentry != null && p_node.symtabentry.symbolDataType == INT) || p_node.symtabentry.m_type.equals(Terminal.INT.getData()))
                size = 4;
            else if (p_node.getType() != null && (p_node.symtabentry.symbolDataType == FLOAT || p_node.getType().equals(Terminal.FLOAT.getData())))
                size = 8;

            // if it is an array, multiply by all dimension sizes
            if (p_node.symtabentry.varDimensionSize > 0) {
                for (Node dim : p_node.getChildren().get(2).getChildren()) {
                    size *= Integer.parseInt(dim.getData());
                }
            }
        } catch (Exception ignored) {

        }
        return size;
    }

    public int sizeOfEntry(SymTabEntry p_nodeTabEntry) {
        int size = 0;
        if ((p_nodeTabEntry.symbolDataType == INT))
            size = 4;
        else if ((p_nodeTabEntry.symbolDataType == FLOAT))
            size = 8;
        // if it is an array, multiply by all dimension sizes
        if (p_nodeTabEntry.varDimensionSize > 0) {
            for (int dim : p_nodeTabEntry.dimList) {
                size *= (dim);
            }
        }
        return size;
    }

    public int sizeOfTypeNode(Node p_node) {
        int size = 0;
        try {
            if (p_node.symtabentry.symbolDataType == INT || p_node.getType().equals(Terminal.INT.getData()))
                size = 4;
            else if (p_node.symtabentry.symbolDataType == FLOAT || p_node.getType().equals(Terminal.FLOAT.getData()))
                size = 8;
        } catch (Exception ignored) {

        }
        return size;
    }

    public void visit(MainNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        if (!this.m_outputfilename.isEmpty()) {
            File file = new File(this.m_outputfilename);
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(p_node.symtab);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visit(ProgramBlockNode node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        for (SymTabEntry entry : node.symtab.m_symlist) {
            entry.m_offset = node.symtab.m_size - entry.m_size;
            node.symtab.m_size -= entry.m_size;
        }
    }


    public void visit(ClassNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        // compute total size and offsets along the way
        // this should be node on all nodes that represent
        // a scope and contain their own table

        p_node.symtab.m_size = 0;

        for (SymTabEntry entry : p_node.symtab.m_symlist) {
            entry.m_offset = p_node.symtab.m_size - entry.m_size;
            p_node.symtab.m_size -= entry.m_size;
        }
    }

    public void visit(FuncDefNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        // compute total size and offsets along the way
        // this should be node on all nodes that represent
        // a scope and contain their own table
        // stack frame contains the return value at the bottom of the stack
        p_node.symtab.m_size = -(this.sizeOfTypeNode(p_node.getChildren().get(0)));
        //then is the return addess is stored on the stack frame
        p_node.symtab.m_size -= 4;
        for (SymTabEntry entry : p_node.symtab.m_symlist) {
            entry.m_offset = p_node.symtab.m_size - entry.m_size;
            p_node.symtab.m_size -= entry.m_size;
        }
    }

    public void visit(VarDeclNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        // determine the size for basic variables

        generateSizeOfVarNode(p_node);

    }

    private void generateSizeOfVarNode(Node p_node) {
        switch (p_node.symtabentry.symbolDataType) {
            case FLOAT:
            case INT:
                p_node.symtabentry.m_size = this.sizeOfEntry(p_node);
                break;
            case CLASS:
                int size = getSizeOfClassObject(p_node.symtabentry.extraData);
                if (p_node.symtabentry.varDimensionSize > 0) {
                    for (Node dim : p_node.getChildren().get(2).getChildren()) {
                        size *= Integer.parseInt(dim.getData());
                    }
                }
                p_node.symtabentry.m_size = size;
                break;
        }
    }

    @Override
    public void visit(ForStatNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        SymTab symTab = node.symtab;
        while (symTab.m_name.equals("For")) {
            symTab = symTab.m_uppertable;
        }

        for (SymTabEntry entry : node.symtab.m_symlist) {
            entry.m_offset = symTab.m_size - entry.m_size;
            symTab.m_size -= entry.m_size;
        }
    }

    public void visit(FParamNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        // determine the size for basic variables
        generateSizeOfVarNode(p_node);
    }

    public void visit(AddOpNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        p_node.symtabentry.m_size = this.sizeOfEntry(p_node);
    }

    public void visit(MultOpNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        p_node.symtabentry.m_size = this.sizeOfEntry(p_node);
    }

    @Override
    public void visit(TypeNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

    }


    @Override
    public void visit(ArithExprNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.symtabentry != null) {
            node.symtabentry.m_size = this.sizeOfEntry(node);
        }

    }

    public void visit(FCallNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        p_node.symtabentry.m_size = this.sizeOfEntry(p_node);
    }

    public void visit(NumNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        p_node.symtabentry.m_size = this.sizeOfEntry(p_node);
    }


    // Below are the visit methods for node types for which this visitor does
    // not apply. They still have to propagate acceptance of the visitor to
    // their children.

    @Override
    public void visit(WriteStatNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(RelExprNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.symtabentry != null) {
            node.symtabentry.m_size = this.sizeOfEntry(node);
        }

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
    public void visit(StatementNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(TermNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }


    @Override
    public void visit(DataMemberNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(DimListNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(ExprNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(FactorNode node) {
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


        if (node.symtabentry != null) {
            node.symtabentry.m_size = this.sizeOfEntry(node);
        }
    }

    @Override
    public void visit(IfStatNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }

    @Override
    public void visit(IndexListNode node) {
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
    public void visit(VarElementNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);

        if (node.symtabentry != null) {
            node.symtabentry.m_size = this.sizeOfEntry(node);
        }

    }

    @Override
    public void visit(VarNode node) {
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
    public void visit(AParamsNode node) {
// propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : node.getChildren())
            child.accept(this);


    }


    @Override
    public void visit(AssignStatNode node) {
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
    public void visit(FParamListNode node) {
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


}
