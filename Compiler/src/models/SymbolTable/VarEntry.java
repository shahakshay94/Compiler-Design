package models.SymbolTable;

import java.util.List;

public class VarEntry extends SymTabEntry {
		
	public VarEntry(SymbolType p_kind, String p_type, String p_name, List<Integer> p_dims){
		super(p_kind, p_type, p_name, null);
		dimList = p_dims;
	}
		
	public String toString(){
		return 	String.format("%-12s" , "| " + symbolType) +
				String.format("%-12s" , "| " + symbolName) +
				String.format("%-12s"  , "| " + m_type) + 
				String.format("%-8s"  , "| " + m_size) + 
				String.format("%-8s"  , "| " + m_offset * -1)
		        + "|";
	}
}
