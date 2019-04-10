package models.AST;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import models.SymbolTable.SymTab;
import models.SymbolTable.SymTabEntry;
import models.Visitors.Visitor;

public abstract class Node {
	
    private List<Node> children    = new ArrayList<Node>();
    private Node parent            = null;
    private String data            = null;
    private static int nodelevel   = 0;
    private String nodeCategory;
    public int lineNumber=-1;
    public int colNumber=-1;
    // The following data members have been added
    // during the implementation of the visitors
    // These could be added using a decorator pattern
    // triggered by a visitor
    private String type            = null;
    private String subtreeString   = null;
    public String m_moonVarName;


    // introduced by symbol table creation visitor
    public SymTab symtab           = null;
    public SymTabEntry symtabentry = null;

    public Node(String data) {
        this.setData(data);
    }

    public Node(String data, Node parent) {
        this.setData(data);
        this.setParent(parent);
        parent.addChild(this);
    }
    
    public Node(String data, String type) {
        this.setData(data);
        this.setType(type);
    }

    public void addAllChildInReverse(List<Node> nodeList) {
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            this.addChild(nodeList.get(i));
        }
    }
    public void setDataFromAllChildReverse(List<Node> nodeList) {
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            this.setData(this.getData()+ "["+nodeList.get(i).getData()+"]");
        }
    }
    public void setDataFromAllChild(List<Node> nodeList,String appendLeft,String appendRight) {
        for (Node node:nodeList) {
            this.setData(this.getData()+ appendLeft +node.getData()+appendRight);
        }
    }

    public void generatePosition(){
        generatePosition(this);
    }
    private void generatePosition(Node node){
        if(node==null){
            return;
        }
        if(node.lineNumber !=-1){
            this.lineNumber = node.lineNumber;
            this.colNumber = node.colNumber;
            return;
        }
        generatePosition(node.parent);
    }

    public SymTab findParentSymTab(Node node){
        if(node==null){
            return null;
        }
        if(node.getParent()==null){
            return null;
        }
        if(node.getParent().symtab!=null){
            return node.getParent().symtab;
        }
        return findParentSymTab(node.getParent());
    }


    public String getNodeCategory() {
        return nodeCategory;
    }

    public void setNodeCategory(String nodeCategory) {
        this.nodeCategory = nodeCategory;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public void addChild(Node child) {
        child.setParent(this);
        this.children.add(child);
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtreeString() {
        return this.subtreeString;
    }

    public void setSubtreeString(String data) {
        this.subtreeString = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        if(this.children.size() == 0) 
            return true;
        else 
            return false;
    }

    public void removeParent() {
        this.parent = null;
    }
    public void print(){
    	System.out.println("===================================================================================");
    	System.out.println("Node type                 | data  | type      | subtreestring | symtabentry");
    	System.out.println("===================================================================================");
    	this.printSubtree();
    	System.out.println("===================================================================================");
    }
    public void print(PrintWriter printWriter){
        printWriter.println("===================================================================================");
        printWriter.println("Node type                 | data  | type      | subtreestring | symtabentry");
        printWriter.println("===================================================================================");
    	this.printSubtree(printWriter);
        printWriter.println("===================================================================================");
    }

    public void printSubtree(PrintWriter printWriter){
    	for (int i = 0; i < Node.nodelevel; i++ )
            printWriter.print("  ");
    	
    	String toprint = String.format("%-100s" , this.getClass().getSimpleName());
    	for (int i = 0; i < Node.nodelevel; i++ )
    		toprint = toprint.substring(0, toprint.length() - 2);
    	toprint += String.format("%-20s" , (this.getData() == null || this.getData().isEmpty())         ? " | " : " | " + this.getData());
    	toprint += String.format("%-12s" , (this.getType() == null || this.getType().isEmpty())         ? " | " : " | " + this.getType());
    	toprint += String.format("%-16s" , (this.subtreeString == null || this.subtreeString.isEmpty()) ? " | " : " | " + this.subtreeString);
    	toprint += (this.symtabentry == null)                                   ? " | " : " | " + this.symtabentry.m_entry;


        printWriter.println(toprint);
    	
    	Node.nodelevel++;
    	List<Node> children = this.getChildren();
		for (int i = 0; i < children.size(); i++ ){
			children.get(i).printSubtree(printWriter);
		}
		Node.nodelevel--;
    }
    public void printSubtree(){
    	for (int i = 0; i < Node.nodelevel; i++ )
    		System.out.print("  ");

    	String toprint = String.format("%-100s" , this.getClass().getSimpleName());
    	for (int i = 0; i < Node.nodelevel; i++ )
    		toprint = toprint.substring(0, toprint.length() - 2);
    	toprint += String.format("%-20s" , (this.getData() == null || this.getData().isEmpty())         ? " | " : " | " + this.getData());
    	toprint += String.format("%-12s" , (this.getType() == null || this.getType().isEmpty())         ? " | " : " | " + this.getType());
    	toprint += String.format("%-16s" , (this.subtreeString == null || this.subtreeString.isEmpty()) ? " | " : " | " + this.subtreeString);
    	toprint += (this.symtabentry == null)                                   ? " | " : " | " + this.symtabentry.m_entry;


    	System.out.println(toprint);

    	Node.nodelevel++;
    	List<Node> children = this.getChildren();
		for (int i = 0; i < children.size(); i++ ){
			children.get(i).printSubtree();
		}
		Node.nodelevel--;
    }

    /**
     * Every class that will be visited needs an accept method, which 
     * then calls the specific visit method in the visitor, achieving
     * double dispatch. 
     */    
    public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}