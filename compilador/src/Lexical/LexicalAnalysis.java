package Lexical;

import java.io.IOException;
import java.io.FileReader;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private FileReader input;
    private char ch;
    
    public LexicalAnalysis(String filename) throws LexicalException {
        try {
            input = new FileReader(filename);
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new SymbolTable();
        line = 1;
        ch = ' ';
    }

    public void close() throws IOException {
        input.close();
    }

    public int getLine() {
        return this.line;
    }

    private void readch() throws IOException {
    	ch = (char) input.read();
    }
    
    
    public Lexeme nextToken() throws IOException {
         Lexeme lex = new Lexeme("", Tag.END_OF_FILE);
         
         for(;;readch()) {
        	 if(ch == ' ' || ch == '\t' || ch == '\r') continue;
        	 else if(ch == '\n') line++;
        	 else if(ch == '{') {
            	 while(ch != '}')readch();
            	 readch();
             }else break;
         }
         
         if(ch == 65535) return lex;
         switch(ch) {
         	case ';':
         		ch = ' ';
         		lex.token=";";
         		lex.type=Tag.SEMICOLON;
         		break;
         	case ',':
         		ch = ' ';
         		lex.token = ",";
         		lex.type= Tag.COLON;
         		break;
         	case '(':
         		ch = ' ';
         		lex.token="(";
         		lex.type=Tag.OPEN_PAR;
         		break;
         	case ')':
         		ch = ' ';
         		lex.token = ")";
         		lex.type= Tag.CLOSE_PAR;
         		break;
         	case '+':
         		ch = ' ';
         		lex.token = "+";
         		lex.type= Tag.PLUS;
         		break;
         	case '-':
         		ch = ' ';
         		lex.token = "-";
         		lex.type= Tag.MINUS;
         		break;
         	case '*':
         		ch = ' ';
         		lex.token = "*";
         		lex.type= Tag.MULT;
         		break;
         	case '/':
         		ch = ' ';
         		lex.token = "/";
         		lex.type= Tag.DIV;
         		break;
         	case '=':
         		lex.token+=ch;
         		readch();
         		if(ch == '=') {
             		lex.token+=ch;
             		ch = ' ';
             		lex.type = Tag.EQUAL;
         		}else {
         			lex.type = Tag.ASSIGN;
         		}
         		break;
         	case '>':
         		lex.token+=ch;
         		readch();
         		if(ch == '=') {
         			lex.token+=ch;
         			ch = ' ';
         			lex.type = Tag.GREATER_EQUAL;
         		}else {
         			lex.type = Tag.GREATER;
         		}
         		break;
         	case '<':
         		lex.token+=ch;
         		readch();
         		if(ch == '=') {
         			lex.token+=ch;
         			ch = ' ';
         			lex.type = Tag.LESSER_EQUAL;
         		}else if(ch == '>'){
         			lex.token+=ch;
         			ch = ' ';
         			lex.type = Tag.DIFF;
         		}else {
         			lex.type = Tag.LESSER;
         		}
         		break;
         }
         if(lex.token != "") return lex;
         if(ch == '"') {
        	 readch();
        	 lex.type = Tag.STRING_C;
        	 while(ch != '"') {
        		 if(ch == 65535) {
        			 lex.type = Tag.UNEXPECTED_EOF;
        		 }
        		 if(ch == '\n') {
        			 
        			 lex.type = Tag.INVALID_TOKEN;
        			 break;
        		 }else {
        			 lex.token+= ch;
        		 }
        		 readch();
        	 }
        	ch = ' ';
         }
         if(lex.token != "") return lex;
         
         if(Character.isDigit(ch)) {
        	 lex.token +=ch;
        	 lex.type = Tag.INT_C;
        	 readch();
        	 while(Character.isDigit(ch)) {
        		 lex.token +=ch;
        		 readch();
        	 }
        	 if(ch == '.') {
        		 lex.type = Tag.FLOAT_C;
        		 lex.token +=ch;
        		 readch();
        		 if(Character.isDigit(ch)) {
        			 while(Character.isDigit(ch)) {
        				 lex.token +=ch;
            			 readch();
            			 
        			 }
        		 }else {
        			 lex.token+=ch;
        			 lex.type = Tag.INVALID_TOKEN;
        		 }
        	 }
        	 if(Character.isLetter(ch)) {
        		 lex.token+=ch;
        		 lex.type = Tag.INVALID_TOKEN;
        	 }
         }
         if(lex.token != "") return lex;
         
         if(Character.isLetter(ch)) {
        	 //lex.token+=ch;
        	 lex.type = Tag.IDENTIFIER;
        	 while(Character.isLetter(ch) || Character.isDigit(ch)) {
				 lex.token +=ch;
    			 readch();
			 }
        	 if(st.contains(lex.token)) {
        		 lex.type = st.find(lex.token);
        	 }else {
        		 st.insert(lex.token);
        	 }
         }
         if(lex.token == "") {
        	 lex.token+=ch;
        	 lex.type = Tag.INVALID_TOKEN;
         }
         return lex;
    }
}
