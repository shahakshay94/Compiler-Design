package models.SymbolTable;

public class ForEntry extends SymTabEntry {


	public ForEntry(String p_type, String p_name, SymTab p_table){
		super(SymbolType.FOR, p_type, p_name, p_table);
	}

	public String toString(){
		return 	String.format("%-12s" , "| " + symbolType) +
				String.format("%-12s" , "| " + symbolName) +
				"|" +
				m_subtable;
	}	
}
