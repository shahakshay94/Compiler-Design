package models.Visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.Terminal;
import models.AST.AParamsNode;
import models.AST.AddOpNode;
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
import models.AST.FactorSignNode;
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
import models.AST.ParamListNode;
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
import models.SymbolTable.ClassEntry;
import models.SymbolTable.ForEntry;
import models.SymbolTable.FuncEntry;
import models.SymbolTable.SymTab;
import models.SymbolTable.SymTabEntry;
import models.SymbolTable.VarEntry;
import utils.LexicalResponseManager;

/**
 * Visitor to create symbol tables and their entries.
 * <p>
 * This concerns only nodes that either:
 * <p>
 * (1) represent identifier declarations/definitions, in which case they need to assemble
 * a symbol table record to be inserted in a symbol table. These are:  VarDeclNode, ClassNode,
 * and FuncDefNode.
 * <p>
 * (2) represent a scope, in which case they need to create a new symbol table, and then
 * insert the symbol table entries they get from their children. These are:  ProgNode, ClassNode,
 * FuncDefNode, and StatBlockNode.
 */

public class SymTabCreationVisitor extends Visitor {

    public Integer m_tempVarNum = 0;

    public String getNewTempVarName() {
        m_tempVarNum++;
        return "t" + m_tempVarNum.toString();
    }

    public void addUniqueSymTabFromInheritence(List<SymTabEntry> intoSymTabEntryList, List<SymTabEntry> fromSymTabEntryList) {
        for (SymTabEntry symTabEntry : fromSymTabEntryList) {
            if (!intoSymTabEntryList.contains(symTabEntry)) {
                intoSymTabEntryList.add(symTabEntry);
            }
        }
    }

    public void checkShadowedVarDecl(SymTab symTab) {
        for (SymTabEntry tabEntry : symTab.m_symlist) {
            if (tabEntry.symbolType == SymTabEntry.SymbolType.CLASS) {
                for (SymTabEntry currentClassSymTab : tabEntry.m_subtable.m_symlist) {
                    for (SymTabEntry inheritedTabEntry : tabEntry.multiLevelInheritedSymTab) {
                        for (SymTabEntry inheritedTable : inheritedTabEntry.m_subtable.m_symlist) {
                            if (inheritedTable.m_entry.equals(currentClassSymTab.m_entry)) {
                                currentClassSymTab.createdFromNode.generatePosition();
                                LexicalResponseManager.getInstance().addErrorMessage(currentClassSymTab.createdFromNode.lineNumber, currentClassSymTab.createdFromNode.colNumber, "Semantic Warning", "Shadowed variable:: Class: " + tabEntry.m_subtable.m_name + " : " + currentClassSymTab.m_entry + " Already declared in Class: " + inheritedTabEntry.m_subtable.m_name);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isCircularDependence(SymTabEntry symTabEntry, List<SymTabEntry> classSymListTraversal) {
        for (SymTabEntry inheritedSymTab : symTabEntry.inheritedSymTab) {
            if (classSymListTraversal.contains(inheritedSymTab)) {
                return true;
            }
            classSymListTraversal.add(inheritedSymTab);
        }
        for (SymTabEntry inheritedSymTab : symTabEntry.inheritedSymTab) {
            if (isCircularDependence(inheritedSymTab, classSymListTraversal)) {
                return true;
            }
        }
        return false;
    }


    public void visit(MainNode node) {
        System.out.println("visiting MainNode");
        node.symtab = new SymTab(0, "global", null);

        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

        // for function definitions, loop over all function definition nodes
        for (Node fndefelt : node.getChildren().get(1).getChildren()) {
            //add the symbol table entry of each function definition in the global symbol table

            // check for scop spec
            if (fndefelt.getChildren().get(1).getData() == null || fndefelt.getChildren().get(1).getData().isEmpty()) {
                node.symtab.addEntry(fndefelt.symtabentry);
            }

            // if scope spec is available then add symbol table point to class funcdecl ..
            else {
                boolean isClassDeclFound = false;
                for (Node classelt : node.getChildren().get(0).getChildren()) {
                    if (classelt.getChildren().get(0).getData().equals(fndefelt.getChildren().get(1).getData())) {
                        for (SymTabEntry symTabEntry : classelt.symtab.m_symlist) {
                            if (symTabEntry.m_entry.contains("function") && fndefelt.symtabentry.m_entry.equals(symTabEntry.m_entry)) {
                                symTabEntry.m_entry = fndefelt.symtabentry.m_entry;
                                symTabEntry.m_subtable = fndefelt.symtabentry.m_subtable;
                                isClassDeclFound = true;
                            }
                        }
                    }
                }
                if (!isClassDeclFound) {
                    // Report symentic error func decl not found ...
                    fndefelt.generatePosition();
                    LexicalResponseManager.getInstance().addErrorMessage(fndefelt.lineNumber, fndefelt.colNumber, "SemanticError", "Could not resolve symbol :" + fndefelt.getChildren().get(1).getData());
                }
            }
        }

        // add inherited symbol list in class symtab entry ::
        for (Node classNode : node.getChildren().get(0).getChildren()) {
            for (Node inheritedNode : classNode.getChildren().get(1).getChildren()) {
                boolean clasSymTabFound = false;
                for (SymTabEntry symTabEntry : node.symtab.m_symlist) {
                    if (symTabEntry.symbolType == SymTabEntry.SymbolType.CLASS && symTabEntry.m_subtable != null && symTabEntry.m_subtable.m_name.equals(inheritedNode.getData())) {
                        // Circular dependence at first level: Check if already entry exist
                        //TODO test below condition not sure it will work..
                        clasSymTabFound = true;
                        if (classNode.symtabentry.inheritedSymTab.contains(symTabEntry)) {
                            inheritedNode.generatePosition();
                            LexicalResponseManager.getInstance().addErrorMessage(inheritedNode.lineNumber, inheritedNode.colNumber, "SemanticError", "Multiple times inherited same class " + inheritedNode.getData() + " In class :" + classNode.getChildren().get(0).getData());
                        } else {
                            classNode.symtabentry.addInheritedSymTab(symTabEntry);
                        }
                        break;
                    }
                }
                if (!clasSymTabFound) {
                    classNode.generatePosition();
                    LexicalResponseManager.getInstance().addErrorMessage(classNode.lineNumber, classNode.colNumber, "SemanticError", "Could not resolve symbol " + inheritedNode.getData());
                }
            }
        }

        // check for circular class dependence at multi level
        for (SymTabEntry symTabEntry : node.symtab.m_symlist) {
            if (symTabEntry.symbolType == SymTabEntry.SymbolType.CLASS) {
                boolean isCircular = false;
                List<SymTabEntry> allInheritedSymTab = new ArrayList<>();
                for (SymTabEntry symTab : symTabEntry.inheritedSymTab) {
                    List<SymTabEntry> symTabEntryList = new ArrayList<>();
                    symTabEntryList.add(symTab);
                    if (isCircularDependence(symTab, symTabEntryList)) {
                        isCircular = true;
                    }
                    addUniqueSymTabFromInheritence(allInheritedSymTab, symTabEntryList);
                }
                symTabEntry.multiLevelInheritedSymTab = allInheritedSymTab;
                if (isCircular) {
                    symTabEntry.createdFromNode.generatePosition();
                    LexicalResponseManager.getInstance().addErrorMessage(symTabEntry.createdFromNode.lineNumber, symTabEntry.createdFromNode.colNumber, "SemanticError", "Circular class dependencies found In class inheritance :" + symTabEntry.m_entry);
                }
            }
        }


        // for the program function, get its local symbol table from node 2 and create an entry for it in the global symbol table
        // first, get the table and change its name
        /*SymTab table = node.getChildren().get(2).symtab;
       
        node.symtab.addEntry("function:program", table);*/
        checkShadowedVarDecl(node.symtab);
    }


    public void visit(StatBlockNode node) {
        System.out.println("visiting StatNode");
        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }
    }

    public void visit(ProgramBlockNode node) {
        System.out.println("visiting Main StatBlockNode");
        SymTab localtable = new SymTab(1, "main", node.symtab);
        node.symtabentry = new FuncEntry("void", "main", new Vector<VarEntry>(), localtable);
        node.symtabentry.m_entry = "main";
        node.symtab.addEntry(node.symtabentry);
        node.symtab = localtable;

        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }

    }


    public void visit(FuncDeclNode node) {
        System.out.println("visiting FuncDeclNode");

        String ftype = node.getChildren().get(0).getData();
        String fname = node.getChildren().get(1).getData();

        SymTab localtable = new SymTab(1, fname, node.symtab);
        node.symtab = localtable;

        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }

        String declrecstring;
        declrecstring = "function:";
        // function return value
        declrecstring += node.getChildren().get(0).getData() + ':';
        // function name
        declrecstring += node.getChildren().get(1).getData() + ':';
        // loop over function parameter list
        String extraData = "";
        Vector<VarEntry> paramlist = new Vector<VarEntry>();
        for (Node param : node.getChildren().get(2).getChildren()) {
            if (param.getChildren().get(2).getChildren().size() > 0) {
                extraData += param.getChildren().get(0).getData() + ":" + param.getChildren().get(2).getChildren().size() + "_";
            } else {
                extraData += param.getChildren().get(0).getData() + "_";
            }
            // parameter type
            declrecstring += param.getChildren().get(0).getData() + ':';
            // parameter name
            declrecstring += param.getChildren().get(1).getData() + ':';
            // parameter dimension list
            for (Node dim : param.getChildren().get(2).getChildren()) {
                // parameter dimension
                declrecstring += dim.getData() + ':';
            }
            paramlist.add((VarEntry) param.symtabentry);
        }

        // the symbol table of the function is the symbol table of its statement block
        // first, get the table and adapt its name to the function
        node.symtabentry = new FuncEntry(ftype, fname, paramlist, localtable);
        node.symtabentry.m_entry = declrecstring;
        node.symtabentry.extraData = extraData;
        node.symtabentry.createdFromNode = node;
        node.symtabentry.symbolName = node.getChildren().get(1).getData();
        node.symtabentry.returnType = node.getChildren().get(0).getData();
        node.symtabentry.symbolType = SymTabEntry.SymbolType.FUNCTION;
        node.symtab.m_uppertable.addEntry(node.symtabentry);
    }


    public void visit(FuncDefNode node) {
        System.out.println("visiting FuncDefNode");
        String ftype = node.getChildren().get(0).getData();
        String fname = node.getChildren().get(2).getData();
        SymTab localtable = new SymTab(1, fname, node.symtab);
        node.symtab = localtable;

        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }

        Vector<VarEntry> paramlist = new Vector<VarEntry>();

        String declrecstring;
        declrecstring = "function:";
        // function return value
        declrecstring += node.getChildren().get(0).getData() + ':';
        // function name
        declrecstring += node.getChildren().get(2).getData() + ':';
        // loop over function parameter list
        String extraData = "";
        for (Node param : node.getChildren().get(3).getChildren()) {
            extraData += param.getChildren().get(0).getData();
            if (param.getChildren().get(2).getChildren().size() > 0) {
                extraData += ":" + param.getChildren().get(2).getChildren().size();
            }
            extraData += "_";
            // parameter type
            declrecstring += param.getChildren().get(0).getData() + ':';
            // parameter name
            declrecstring += param.getChildren().get(1).getData() + ':';
            // parameter dimension list
            for (Node dim : param.getChildren().get(2).getChildren()) {
                // parameter dimension
                declrecstring += dim.getData() + ':';
            }
            paramlist.add((VarEntry) param.symtabentry);
        }

        node.symtabentry = new FuncEntry(ftype, fname, paramlist, localtable);
        node.symtabentry.m_entry = declrecstring;
        node.symtabentry.extraData = extraData;
        node.symtabentry.symbolName = node.getChildren().get(2).getData();
        node.symtabentry.returnType = node.getChildren().get(0).getData();
        node.symtabentry.symbolType = SymTabEntry.SymbolType.FUNCTION;
        node.symtabentry.createdFromNode = node;

    }


    public void visit(ClassNode node) {
        System.out.println("visiting ClassNode");
        // get the class name from node 0
        String classname = node.getChildren().get(0).getData();
        SymTab localtable = new SymTab(1, classname, node.symtab);
        node.symtabentry = new ClassEntry(classname, localtable);
        node.symtabentry.m_entry = classname;
        node.symtabentry.symbolType = SymTabEntry.SymbolType.CLASS;
        node.symtabentry.createdFromNode = node;

        boolean isEntryExist = false;
        for (SymTabEntry symTab : node.symtab.m_symlist) {
            try {
                if (symTab.symbolType == SymTabEntry.SymbolType.CLASS && symTab.m_subtable.m_name.equals(classname)) {
                    node.generatePosition();
                    LexicalResponseManager.getInstance().addErrorMessage(node.lineNumber, node.colNumber, "SemanticError", "Class multiple declaration :" + symTab.m_subtable.m_name);
                    isEntryExist = true;
                }
            } catch (Exception ex) {
            }
        }
        if (!isEntryExist) {
            node.symtab.addEntry(node.symtabentry);
        }
        node.symtab = localtable;

        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }


    }


    public void visit(ForStatNode node) {
        System.out.println("visiting ForStatNode");

        // aggregate information from the subtree
        String declrecstring;
        // identify what kind of record that is
        declrecstring = "localvar:";
        // get the type from the first child node and aggregate here
        declrecstring += node.getChildren().get(0).getData() + ':';
        // get the id from the second child node and aggregate here
        declrecstring += node.getChildren().get(1).getData() + ':';

        SymTab localtable = new SymTab(2, "For", node.symtab);
        node.symtabentry = new ForEntry("", "For", localtable);

//        node.symtabentry = new VarEntry(SymTabEntry.SymbolType.VARIABLE, node.getChildren().get(0).getData(), node.getChildren().get(1).getData() /*Sym Name : var name */, new ArrayList<>());
        node.symtabentry.m_entry = declrecstring;
        node.symtabentry.extraData = "For"; // type
        node.symtabentry.symbolType = SymTabEntry.SymbolType.FOR;
        node.symtabentry.createdFromNode = node;
        node.symtab.addEntry(node.symtabentry);
        node.symtab = localtable;


        SymTabEntry symTabEntry = new VarEntry(SymTabEntry.SymbolType.VARIABLE, node.getChildren().get(0).getData(), node.getChildren().get(1).getData() /*Sym Name : var name */, new ArrayList<>());
        symTabEntry.extraData = node.getChildren().get(0).getData(); // type
        symTabEntry.symbolType = SymTabEntry.SymbolType.VARIABLE;
        symTabEntry.createdFromNode = node;
        node.symtab.addEntry(symTabEntry);
        node.getChildren().get(1).symtabentry = symTabEntry;
        node.getChildren().get(1).m_moonVarName = node.getChildren().get(1).getData();
        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }
    }


    public void visit(VarDeclNode node) {

        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }

        System.out.println("visiting VarDeclNode");
        List<Integer> dimList = new ArrayList<>();
        // aggregate information from the subtree
        String declrecstring;
        String type = "";
        // identify what kind of record that is
        declrecstring = "localvar:";
        // get the type from the first child node and aggregate here
        declrecstring += node.getChildren().get(0).getData() + ':';
        type += node.getChildren().get(0).getData();
        // get the id from the second child node and aggregate here
        declrecstring += node.getChildren().get(1).getData() + ':';
        // loop over the list of dimension nodes and aggregate here
        for (Node dim : node.getChildren().get(2).getChildren()) {
            declrecstring += dim.getData() + ':';
            dimList.add(Integer.parseInt(dim.getData()));
            type += "[" + dim.getData() + "]";
            if (!dim.getType().equals(Terminal.INT.getData())) {
                dim.generatePosition();
                LexicalResponseManager.getInstance().addErrorMessage(dim.lineNumber, dim.colNumber, "SemanticError", "Incompatible types: Required Int: Found" + dim.getType());
            }
        }
        // create the symbol table entry for this variable
        // it will be picked-up by another node above later

        node.setType(type);
        node.symtabentry = new VarEntry(SymTabEntry.SymbolType.VARIABLE, node.getChildren().get(0).getData(), node.getChildren().get(1).getData() /*Sym Name : var name */, dimList);
        node.symtabentry.m_entry = declrecstring;
        node.symtabentry.createdFromNode = node;
        node.symtabentry.extraData = node.getChildren().get(0).getData(); // type
        node.symtabentry.symbolName = node.getChildren().get(1).getData(); // var name
        node.symtabentry.varDimensionSize = node.getChildren().get(2).getChildren().size();
        node.symtabentry.dimList = dimList;
        node.symtabentry.symbolType = SymTabEntry.SymbolType.VARIABLE;

        switch (node.getChildren().get(0).getData()) {
            case "integer":
                node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.INT;
                break;
            case "float":
                node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.FLOAT;
                break;
            default:
                node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.CLASS;
        }
        addSymbolEntryInSymbolTable(node.symtab, node.symtabentry);
    }


    public void visit(FParamNode node) {
        for (Node child : node.getChildren()) {
            child.symtab = node.symtab;
            child.accept(this);
        }

        List<Integer> dimList = new ArrayList<>();
        System.out.println("visiting FParamNode");
        // aggregate information from the subtree
        String declrecstring;
        // identify what kind of record that is
        declrecstring = "localvar:";
        // get the type from the first child node and aggregate here
        declrecstring += node.getChildren().get(0).getData() + ':';
        // get the id from the second child node and aggregate here
        declrecstring += node.getChildren().get(1).getData() + ':';
        // loop over the list of dimension nodes and aggregate here
        for (Node dim : node.getChildren().get(2).getChildren()) {
            declrecstring += dim.getData() + ':';
            dimList.add(Integer.parseInt(dim.getData()));
        }
        // create the symbol table entry for this variable
        // it will be picked-up by another node above later
        node.symtabentry = new VarEntry(SymTabEntry.SymbolType.PARAMETER, node.getChildren().get(0).getData(), node.getChildren().get(1).getData() /*Sym Name : var name */, dimList);
        node.symtabentry.m_entry = declrecstring;
        node.symtabentry.createdFromNode = node;
        node.symtabentry.extraData = node.getChildren().get(0).getData(); // type
        node.symtabentry.symbolName = node.getChildren().get(1).getData(); // var name
        node.symtabentry.varDimensionSize = node.getChildren().get(2).getChildren().size();
        node.symtabentry.dimList = dimList;
        node.symtabentry.symbolType = SymTabEntry.SymbolType.PARAMETER;

        switch (node.getChildren().get(0).getData()) {
            case "integer":
                node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.INT;
                break;
            case "float":
                node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.FLOAT;
                break;
            default:
                node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.CLASS;
        }

        addFparamEntryInSymbolTable(node.symtab, node.symtabentry);
    }


    public void addSymbolEntryInSymbolTable(SymTab symTab, SymTabEntry needToAddSymTabEntry) {
        Node createdFromNode = needToAddSymTabEntry.createdFromNode;
        boolean isSymAlreadyDeclared = false;
        for (SymTabEntry symTabEntry : symTab.m_symlist) {
            if (symTabEntry.m_entry.equals(needToAddSymTabEntry.m_entry)) {
                createdFromNode.generatePosition();
                LexicalResponseManager.getInstance().addErrorMessage(createdFromNode.lineNumber, createdFromNode.colNumber, "SemanticError", "Multiple declaration of :" + createdFromNode.symtabentry.m_entry);
                System.out.println("Multiple declaration of :" + createdFromNode.symtabentry.m_entry);
                isSymAlreadyDeclared = true;
            }
        }
        if (!isSymAlreadyDeclared) {
            symTab.addEntry(needToAddSymTabEntry);
        }
    }
    public void addFparamEntryInSymbolTable(SymTab symTab, SymTabEntry needToAddSymTabEntry) {
        Node createdFromNode = needToAddSymTabEntry.createdFromNode;
        boolean isSymAlreadyDeclared = false;
        int fparamsCount=0;
        for (SymTabEntry symTabEntry : symTab.m_symlist) {
            if(symTabEntry.symbolType == SymTabEntry.SymbolType.PARAMETER){
                fparamsCount++;
            }
            if (symTabEntry.m_entry.equals(needToAddSymTabEntry.m_entry)) {
                createdFromNode.generatePosition();
                LexicalResponseManager.getInstance().addErrorMessage(createdFromNode.lineNumber, createdFromNode.colNumber, "SemanticError", "Multiple declaration of :" + createdFromNode.symtabentry.m_entry);
                System.out.println("Multiple declaration of :" + createdFromNode.symtabentry.m_entry);
                isSymAlreadyDeclared = true;
            }
        }
        if (!isSymAlreadyDeclared) {
            symTab.addEntry(fparamsCount,needToAddSymTabEntry);
        }
    }

    public boolean isSymbolAlreadyExist(SymTab symTab, SymTabEntry needToAddSymTabEntry,boolean needToThrowError){
        Node createdFromNode = needToAddSymTabEntry.createdFromNode;
        boolean isSymAlreadyDeclared = false;
        for (SymTabEntry symTabEntry : symTab.m_symlist) {
            if (symTabEntry.m_entry.equals(needToAddSymTabEntry.m_entry)) {
                if(needToThrowError) {
                    createdFromNode.generatePosition();
                    LexicalResponseManager.getInstance().addErrorMessage(createdFromNode.lineNumber, createdFromNode.colNumber, "SemanticError", "Multiple declaration of :" + createdFromNode.symtabentry.m_entry);
                    System.out.println("Multiple declaration of :" + createdFromNode.symtabentry.m_entry);
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public void visit(IdNode p_node) {
        for (Node child : p_node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = p_node.symtab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getData();
    }

    @Override
    public void visit(ArithExprNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = p_node.symtab;
            child.accept(this);
        }
        // only term in arithexpr node ///
        if (p_node.getChildren().size() == 1) {
            return;
        }
        // multop as child ..
        String tempvarname = this.getNewTempVarName();
        p_node.m_moonVarName = tempvarname;
        p_node.symtabentry = new VarEntry(SymTabEntry.SymbolType.TEMPVAR, p_node.getType(), p_node.m_moonVarName, p_node.symtab.lookupName(p_node.getChildren().get(0).m_moonVarName).dimList);
        p_node.symtabentry.m_entry = "tempvar:" + tempvarname + " " + p_node.getType();
        p_node.symtab.addEntry(p_node.symtabentry);
    }


    @Override
    public void visit(RelExprNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

        String tempvarname = this.getNewTempVarName();
        Vector<Integer> dimList = new Vector<Integer>();
        node.m_moonVarName = tempvarname;
        node.symtabentry = new VarEntry(SymTabEntry.SymbolType.TEMPVAR, node.getType(), node.m_moonVarName, dimList);
        node.symtabentry.m_entry = "tempvar:" + tempvarname + " " + node.getType();
        node.symtab.addEntry(node.symtabentry);
    }

    @Override
    public void visit(VarElementNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

        if (node.getChildren().size() > 0) {
            if (node.getChildren().get(0) instanceof DataMemberNode) {
                Node dataMember = node.getChildren().get(0);
                // introduce new tempvar if datamember have any dimlist ...
                if (dataMember.getChildren().size() > 1 && dataMember.getChildren().get(1).getChildren().size() > 0) {
                    String tempvarname = this.getNewTempVarName();
                    node.m_moonVarName = tempvarname;
                    node.symtabentry = new VarEntry(SymTabEntry.SymbolType.TEMPVAR, "integer", node.m_moonVarName, new ArrayList<>());
                    node.symtabentry.m_entry = "tempvar:" + tempvarname + " " + node.getType();
                    node.symtabentry.symbolDataType = SymTabEntry.SymbolDataType.INT;
                    node.symtab.addEntry(node.symtabentry);
                }
            }
        }
    }

    @Override
    public void visit(MultOpNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren()) {
            child.symtab = p_node.symtab;
            child.accept(this);
        }
        String tempvarname = this.getNewTempVarName();
        p_node.m_moonVarName = tempvarname;
        String vartype = p_node.getType();
        Vector<Integer> dimlist = new Vector<Integer>();
        p_node.symtabentry = new VarEntry(SymTabEntry.SymbolType.TEMPVAR, vartype, p_node.m_moonVarName, dimlist);
        p_node.symtabentry.m_entry = "tempvar:" + tempvarname + " " + p_node.getType();
        p_node.symtab.addEntry(p_node.symtabentry);
    }

    @Override
    public void visit(AddOpNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren()) {
            child.symtab = p_node.symtab;
            child.accept(this);
        }
        String tempvarname = this.getNewTempVarName();
        p_node.m_moonVarName = tempvarname;
        p_node.symtabentry = new VarEntry(SymTabEntry.SymbolType.TEMPVAR, p_node.getType(), p_node.m_moonVarName, p_node.symtab.lookupName(p_node.getChildren().get(0).m_moonVarName).dimList);
        p_node.symtabentry.m_entry = "tempvar:" + tempvarname + " " + p_node.getType();
        p_node.symtab.addEntry(p_node.symtabentry);
    }

    @Override
    public void visit(NumNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren()) {
            child.symtab = p_node.symtab;
            child.accept(this);
        }
        String tempvarname = this.getNewTempVarName();
        p_node.m_moonVarName = tempvarname;
        String vartype = p_node.getType();
        p_node.symtabentry = new VarEntry(SymTabEntry.SymbolType.LITVAL, vartype, p_node.m_moonVarName, new Vector<Integer>());
        p_node.symtabentry.m_entry = "litval:" + tempvarname + " " + p_node.getType();
        p_node.symtab.addEntry(p_node.symtabentry);
    }


    @Override
    public void visit(FCallNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = p_node.symtab;
            child.accept(this);
        }
        String tempvarname = this.getNewTempVarName();
        p_node.m_moonVarName = tempvarname;
        String vartype = p_node.getType();
        p_node.symtabentry = new VarEntry(SymTabEntry.SymbolType.RETVAL, vartype, p_node.m_moonVarName, new Vector<Integer>());
        p_node.symtabentry.m_entry = "retval:" + tempvarname + " " + p_node.getType();
        p_node.symtab.addEntry(p_node.symtabentry);
    }


    @Override
    public void visit(StatementNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(TermNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(TypeNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(FParamListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }


    @Override
    public void visit(VarNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(FuncDefListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(ReadStatNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }


    @Override
    public void visit(IfStatNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(IndexListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(InherListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(MemberListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(Node node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }


    @Override
    public void visit(OpNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(ParamListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }


    @Override
    public void visit(AParamsNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(AssignStatNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(ClassListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(DataMemberNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(DimListNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(ExprNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(FactorNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(FactorSignNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(WriteStatNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }


    @Override
    public void visit(ReturnStatNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

    @Override
    public void visit(ScopeSpecNode node) {
        for (Node child : node.getChildren()) {
            //make all children use this scopes' symbol table
            child.symtab = node.symtab;
            child.accept(this);
        }

    }

}
