package Lexical;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

class SymbolTable {

    private Map<String, Tag> st;

    public SymbolTable() {
        st = new HashMap<String, Tag>();
        
        // keywords
        st.put("start", Tag.START);
        st.put("exit", Tag.EXIT);
        st.put("print", Tag.PRINT);
        st.put("scan", Tag.SCAN);
        st.put("if", Tag.IF);
        st.put("then", Tag.THEN);
        st.put("else", Tag.ELSE);
        st.put("end", Tag.END);
        st.put("do", Tag.DO);
        st.put("while", Tag.WHILE);
        st.put("int", Tag.INT_T);
        st.put("float", Tag.FLOAT_T);
        st.put("string", Tag.STRING_T);
    }

    public boolean insert(String token) {
    	if(contains(token))return false;
    	else{
    		st.put(token, Tag.IDENTIFIER);
    		return true;
    	}
    }
    
    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public Tag find(String token) {
        return this.contains(token) ?
            st.get(token) : Tag.INVALID_TOKEN;
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
