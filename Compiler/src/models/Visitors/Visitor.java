package models.Visitors;

import models.AST.*;

/**
 * Visitor superclass. Can be either an interface or an abstract class.
 * Needs to have one visit method for each of the visit methods
 * implemented by any of its subclasses.
 *
 * This forces all its subclasses
 * to implement all of them, even if they are not concerned with
 * processing of this particular subtype, creating visit methods
 * with an empty function body.
 *
 * In order to avoid having empty visit functions, one can create
 * the Visitor class as a normal class with all visitor methods
 * with empty function bodies.
 *
 * These empty methods are executed in case a specific visitor does
 * not implement a visit method for a specific kind of visitable
 * object on which accept may be called, but no action is required
 * for this specific visitor.
 */

 public abstract class Visitor {

    public abstract void visit(AddOpNode node);
    public abstract void visit(AParamsNode node);
    public abstract void visit(ArithExprNode node);
    public abstract void visit(AssignStatNode node);
    public abstract void visit(ClassListNode node);
    public abstract void visit(ClassNode node);
    public abstract void visit(DataMemberNode node);
    public abstract void visit(DimListNode node);
    public abstract void visit(ExprNode node);
    public abstract void visit(FactorNode node);
    public abstract void visit(FactorSignNode node);
    public abstract void visit(FCallNode node);
    public abstract void visit(ForStatNode node);
    public abstract void visit(FParamListNode node);
    public abstract void visit(FParamNode node);
    public abstract void visit(FuncDeclNode node);
    public abstract void visit(FuncDefListNode node);
    public abstract void visit(FuncDefNode node);
    public abstract void visit(ReadStatNode node);
    public abstract void visit(IdNode node);
    public abstract void visit(IfStatNode node);
    public abstract void visit(IndexListNode node);
    public abstract void visit(InherListNode node);
    public abstract void visit(MemberListNode node);
    public abstract void visit(MultOpNode node);
    public abstract void visit(Node node);
    public abstract void visit(NumNode node);
    public abstract void visit(OpNode node);
    public abstract void visit(ParamListNode node);
    public abstract void visit(MainNode node);
    public abstract void visit(WriteStatNode node);
    public abstract void visit(RelExprNode node);
    public abstract void visit(ReturnStatNode node);
    public abstract void visit(ScopeSpecNode node);
    public abstract void visit(StatBlockNode node);
    public abstract void visit(StatementNode node);
    public abstract void visit(TermNode node);
    public abstract void visit(TypeNode node);
    public abstract void visit(VarDeclNode node);
    public abstract void visit(VarElementNode node);
    public abstract void visit(VarNode node);
    public abstract void visit(ProgramBlockNode node);
 }