package Lexical;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class SymbolTable {

    private Map<String, SymbolTableElement> st;

    public SymbolTable() {
        st = new HashMap<String, SymbolTableElement>();
        
        // keywords
        st.put("start", new SymbolTableElement(Tag.START));
        st.put("exit", new SymbolTableElement(Tag.EXIT));
        st.put("print", new SymbolTableElement(Tag.PRINT));
        st.put("scan", new SymbolTableElement(Tag.SCAN));
        st.put("if", new SymbolTableElement(Tag.IF));
        st.put("then", new SymbolTableElement(Tag.THEN));
        st.put("else", new SymbolTableElement(Tag.ELSE));
        st.put("end", new SymbolTableElement(Tag.END));
        st.put("do", new SymbolTableElement(Tag.DO));
        st.put("while", new SymbolTableElement(Tag.WHILE));
        st.put("int", new SymbolTableElement(Tag.INT_T));
        st.put("float", new SymbolTableElement(Tag.FLOAT_T));
        st.put("string", new SymbolTableElement(Tag.STRING_T));
        st.put("and", new SymbolTableElement(Tag.AND));
        st.put("or", new SymbolTableElement(Tag.OR));
        st.put("not", new SymbolTableElement(Tag.NOT));
    }

    public boolean insert(String token) {
    	if(contains(token))return false;
    	else{
    		st.put(token, new SymbolTableElement(Tag.IDENTIFIER));
    		return true;
    	}
    }
    
    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public SymbolTableElement find(String token) {
        return this.contains(token) ?
            st.get(token) : new SymbolTableElement(Tag.INVALID_TOKEN);
    }
    
    public String toString() {
    	String s = "\nToken=>Tag\n__________________________________\n";
    	Set<String> tokens = st.keySet();
    	for(String token : tokens) {
    		s+=token+"=>"+st.get(token)+"\n";
    	}
    	return s;
    }
}
