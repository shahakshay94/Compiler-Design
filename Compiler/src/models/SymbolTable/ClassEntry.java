package models.SymbolTable;

public class ClassEntry extends SymTabEntry {

	public ClassEntry(String p_name, SymTab p_subtable){
		super(SymbolType.CLASS, p_name, p_name, p_subtable);
	}
		
	public String toString(){
		return 	String.format("%-12s" , "| " + symbolType) +
				String.format("%-40s" , "| " + symbolName) +
				"|" + 
				m_subtable;
	}
	
}

