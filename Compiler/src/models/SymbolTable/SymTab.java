package models.SymbolTable;

import java.util.ArrayList;

public class SymTab {
    public SymTab m_uppertable;
    public String m_name = null;
    public ArrayList<SymTabEntry> m_symlist = null;
    public int tableLevel = 0;
    public int m_size;

    public SymTab() {
        m_name = null;
        m_symlist = new ArrayList<SymTabEntry>();
    }

    public SymTab(String p_name) {
        m_name = p_name;
        m_symlist = new ArrayList<SymTabEntry>();
    }

    public SymTab(int p_level, String p_name, SymTab p_uppertable) {
        tableLevel = p_level;
        m_name = p_name;
        m_symlist = new ArrayList<SymTabEntry>();
        m_uppertable = p_uppertable;
    }


    public void addEntry(SymTabEntry p_entry) {
        m_symlist.add(p_entry);
    }

    public void addEntry(int index, SymTabEntry p_entry) {
        m_symlist.add(index, p_entry);
    }


    public SymTabEntry lookupName(String p_tolookup) {
        SymTabEntry returnvalue = new SymTabEntry();
        boolean found = false;
        for (SymTabEntry rec : m_symlist) {
            if (rec.symbolName.equals(p_tolookup)) {
                returnvalue = rec;
                found = true;
            }
        }
        if (!found) {
            if (m_uppertable != null) {
                returnvalue = m_uppertable.lookupName(p_tolookup);
            }
        }
        return returnvalue;
    }

    public SymTabEntry lookupFunction(String p_tolookup, String params) {
        SymTabEntry returnvalue = new SymTabEntry();
        boolean found = false;
        for (SymTabEntry rec : m_symlist) {
            if (rec.symbolName.equals(p_tolookup) && rec.extraData.equals(params)) {
                returnvalue = rec;
                found = true;
            }
        }
        if (!found) {
            if (m_uppertable != null) {
                returnvalue = m_uppertable.lookupFunction(p_tolookup, params);
            }
        }
        return returnvalue;
    }


    public String toString() {
        String stringtoreturn = new String();
        String prelinespacing = new String();
        for (int i = 0; i < this.tableLevel; i++)
            prelinespacing += "|    ";
        stringtoreturn += "\n" + prelinespacing + "=====================================================\n";
        stringtoreturn += prelinespacing + String.format("%-25s", "| table: " + m_name) + String.format("%-27s", " scope offset: " + Math.abs(m_size)) + "|\n";
        stringtoreturn += prelinespacing + "=====================================================\n";
        for (int i = 0; i < m_symlist.size(); i++) {
            stringtoreturn += prelinespacing + m_symlist.get(i).toString() + '\n';
        }
        stringtoreturn += prelinespacing + "=====================================================";
        return stringtoreturn;
    }


}
