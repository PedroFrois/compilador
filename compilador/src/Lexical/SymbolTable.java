package Lexical;

import java.util.Map;
import java.util.HashMap;

class SymbolTable {

    private Map<String, Tag> st;

    public SymbolTable() {
        st = new HashMap<String, Tag>();
        
        // symbols
        st.put(";", Tag.SEMICOLON);
        st.put(",", Tag.COLON);
        st.put("{", Tag.OPEN_PAR);
        st.put("}", Tag.CLOSE_PAR);
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
        // operators

        st.put("=", Tag.ASSIGN);
        st.put("+", Tag.PLUS);
        st.put("-", Tag.MINUS);
        st.put("*", Tag.MULT);
        st.put("/", Tag.DIV);
        st.put("==", Tag.EQUAL);
        st.put(">", Tag.GREATER);
        st.put(">=", Tag.GREATER_EQUAL);
        st.put("<", Tag.LESSER);
        st.put("<=", Tag.LESSER_EQUAL);
        st.put("<>", Tag.DIFF);
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
}
