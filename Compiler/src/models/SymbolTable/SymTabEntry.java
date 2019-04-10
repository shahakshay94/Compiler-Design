package models.SymbolTable;

import models.AST.IdNode;
import models.AST.Node;

import java.util.ArrayList;
import java.util.List;

public class SymTabEntry {
    public String m_type;
    public String m_entry = null;
    public SymTab m_subtable = null;
    public SymbolType symbolType;
    public SymbolDataType symbolDataType;
    public int varDimensionSize;
    public List<Integer> dimList = new ArrayList<>();
    public String symbolName;
    public String returnType;
    public String extraData;
    public List<SymTabEntry> multiLevelInheritedSymTab = new ArrayList<>();
    public Node createdFromNode = new IdNode("");
    public int m_size;
    public int m_offset;

    public SymTabEntry(String p_entry) {
        m_entry = p_entry;
    }


    public SymTabEntry(SymbolType p_kind, String p_type, String p_name, SymTab p_subtable) {
        symbolType = p_kind;
        m_type = p_type;
        symbolName = p_name;
        m_subtable = p_subtable;
    }

    public SymTabEntry(SymbolType p_kind, String p_type, String p_name) {
        symbolType = p_kind;
        m_type = p_type;
        symbolName = p_name;
    }

    public List<SymTabEntry> inheritedSymTab = new ArrayList<>();

    public SymTabEntry(String p_name, SymTab p_subtable) {
        symbolName = p_name;
        m_subtable = p_subtable;
    }

    public SymTabEntry() {

    }

    public void addInheritedSymTab(SymTabEntry symTab) {
        inheritedSymTab.add(symTab);
    }

    public SymTabEntry lookupClass(SymTabEntry symTabEntry, String p_className) {

        if (symTabEntry.m_subtable != null) {
//			for(SymTabEntry)
        }

        return lookupInheritedClass(symTabEntry, p_className);
    }

    ;

    public SymTabEntry lookupInheritedClass(SymTabEntry symTabEntry, String p_tolookup) {
        for (SymTabEntry rec : inheritedSymTab) {
            if (rec.symbolName.equals(p_tolookup)) {
                return rec;
            }
            return lookupInheritedClass(rec, p_tolookup);
        }
        return null;
    }


    public enum SymbolType {
        FUNCTION,
        CLASS,
        PARAMETER,
        VARIABLE,
        TEMPVAR,
        LITVAL,
        FOR,
        RETVAL;
    }

    public enum SymbolDataType {
        INT,
        FLOAT,
        CLASS;
    }
}
