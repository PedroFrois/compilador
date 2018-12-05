package Syntatical;

import java.io.IOException;

import Lexical.*;

public class Parser {
	private LexicalAnalysis lex;
    private Lexeme current;

    public Parser(LexicalAnalysis lex) throws IOException {
        this.lex = lex;
        this.current = lex.nextToken();
    }

    public void start() throws IOException {
    	program();
    }

    private void eat(Tag type) throws IOException {
        // System.out.println("Match token: " + current.type + " == " + type + "?");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void program() throws IOException {
    	switch(current.type) {
    	case START:
    		eat(Tag.START);declList();stmtList();eat(Tag.EXIT);eat(Tag.END_OF_FILE);
    		break;
    	default:
    		showError();
    	}
    }
    
    private void declList() throws IOException {
    	switch(current.type) {
    	case INT_T:
    	case FLOAT_T:
    	case STRING_T:
    		decl();declList();
    		break;
    	default:
    		return;
    	}
    }
    private IdentifierType decl() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux2;
    	switch(current.type) {
    	case INT_T:
    	case FLOAT_T:
    	case STRING_T:
    		aux = type();aux2 =identList(aux);eat(Tag.SEMICOLON);
    		if(aux == IdentifierType.ERROR || aux2 == IdentifierType.ERROR) {
    			t= IdentifierType.ERROR;
    			showSemanticalError(0);
    		}
    		else
    			t = aux;
    		break;
    	default:
    		showError();
    	}
    	return t;
    }
    
    private IdentifierType identList(IdentifierType t) throws IOException {
    	IdentifierType t2 = IdentifierType.UNDEFINED;
    	IdentifierType aux;
    	switch(current.type) {
    	case IDENTIFIER:
    		String token = current.token;
    		eat(Tag.IDENTIFIER);
    		aux = identListAux(t);
    		if(getIdType(token) != IdentifierType.UNDEFINED || 
    	       aux == IdentifierType.ERROR) {
    			t2 = IdentifierType.ERROR;
    			showSemanticalError(0);
    		}else {
        		setIdType(token, t);    	
        		t2 = t;
    		}
    		break;
		default:
			showError();
    	}
    	return t2;
    }
    
    private IdentifierType identListAux(IdentifierType t) throws IOException {
    	IdentifierType t2 = IdentifierType.UNDEFINED;
    	IdentifierType aux;
    	switch(current.type) {
    	case COMMA:
    		eat(Tag.COMMA);
    		String token = current.token;
    		eat(Tag.IDENTIFIER);
    		aux = identListAux(t);
    		if(getIdType(token) != IdentifierType.UNDEFINED || 
    	       aux == IdentifierType.ERROR) {
    			t2 = IdentifierType.ERROR;
    			showSemanticalError(0);
    		}else {
        		setIdType(token, t);    	
        		t2 = t;
    		}
    		break;
		default:
			break;
    	}
    	return t2;
    }
    
    private IdentifierType type() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
    	case INT_T:
    		t = IdentifierType.INT;
    		eat(Tag.INT_T);
    		break;
    	case FLOAT_T:
    		t = IdentifierType.FLOAT;
    		eat(Tag.FLOAT_T);
    		break;
    	case STRING_T:
    		t = IdentifierType.STRING;
    		eat(Tag.STRING_T);
    		break;
    	default:
    		showError();
    	}
    	return t;
    }
    
    private void stmtList() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case IF:
    	case DO:
    	case SCAN:
    	case PRINT:
    		stmt();stmtListAux();
    		break;
    	default:
    		showError();
    	}
    }
    
    private void stmtListAux() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case IF:
    	case DO:
    	case SCAN:
    	case PRINT:
    		stmt();stmtListAux();
    		break;
    	default:
    		return;
    	}
    }
    
    private void stmt() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    		assignStmt();eat(Tag.SEMICOLON);
    		break;
    	case IF:
    		ifStmt();
    		break;
    	case DO:
    		whileStmt();
    		break;
    	case SCAN:
    		readStmt();eat(Tag.SEMICOLON);
    		break;
    	case PRINT:
    		writeStmt();eat(Tag.SEMICOLON);
    		break;
    	default:
    		showError();
    	}
    }
    
    private IdentifierType assignStmt() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux1;
    	switch(current.type) {
    	case IDENTIFIER:
    		aux = getIdType(current.token);
    		eat(Tag.IDENTIFIER);
    		eat(Tag.ASSIGN);
    		aux1 = simpleExpr();
    		if(aux == IdentifierType.UNDEFINED) {
    			t = IdentifierType.ERROR;
    			showSemanticalError(2);
    		}
    		else if( aux == aux1)
    			t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
    	default:
    		showError();
    	}
    	return t;
    }
    
    private void ifStmt() throws IOException {
    	switch(current.type) {
    	case IF:
    		eat(Tag.IF);condition();eat(Tag.THEN);stmtList();endIfStmt();
    		break;
    	default:
    		showError();
    	}
    }
    
    private void endIfStmt() throws IOException {
    	switch(current.type) {
    	case END:
    		eat(Tag.END);
    		break;
    	case ELSE:
    		eat(Tag.ELSE);stmtList();eat(Tag.END);
    		break;
    	default:
    		showError();
    	}
    }
    private IdentifierType condition() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		if(expression() == IdentifierType.BOOL)
    			t = IdentifierType.BOOL;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
    	default:
    		showError();
    	}
    	return t;
    }
    
    private void whileStmt() throws IOException {
    	switch(current.type) {
    	case DO:
    		eat(Tag.DO);stmtList();whileSuffix();
    		break;
		default:
			showError();
    	}
    }
    
    private void whileSuffix() throws IOException {
    	switch(current.type) {
    	case WHILE:
    		eat(Tag.WHILE);condition();eat(Tag.END);
    		break;
		default:
			showError();
    	}
    }
    
    private void readStmt() throws IOException {
    	IdentifierType aux;
    	switch(current.type) {
    	case SCAN:
    		eat(Tag.SCAN);eat(Tag.OPEN_PAR);
    		aux = getIdType(current.token);
    		if(aux == IdentifierType.UNDEFINED) {
    			showSemanticalError(2);
    		}
    		eat(Tag.IDENTIFIER);eat(Tag.CLOSE_PAR);
    		break;
		default:
			showError();
    	}
    }
    
    private void writeStmt() throws IOException {
    	switch(current.type) {
    	case PRINT:
    		eat(Tag.PRINT);eat(Tag.OPEN_PAR);writable();eat(Tag.CLOSE_PAR);
    		break;
		default:
			showError();
    	}
    }
    
    private IdentifierType writable() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux;
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		aux = simpleExpr();
    		if(aux == IdentifierType.INT || aux == IdentifierType.FLOAT || aux == IdentifierType.STRING)
    			t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
    	default:
    		showError();
    	}
        return t;
    }
    
    private IdentifierType expression() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux1;
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		aux = simpleExpr();
    		aux1 = expressionAux();
    		if(aux1 == IdentifierType.ERROR || aux == IdentifierType.ERROR) {
    			t= IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		if(aux1 == IdentifierType.UNDEFINED)t = aux;
    		else if(aux == aux1) t = IdentifierType.BOOL;
    		else {
    			t= IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
		default:
			showError();
    	}
    	return t;
    }
    private IdentifierType expressionAux() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux;
    	
    	switch(current.type) {
    	case EQUAL:
    	case DIFF:
    		relop();aux=simpleExpr();
    		if(aux == IdentifierType.STRING || aux == IdentifierType.INT || aux == IdentifierType.FLOAT)
    			t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
    	case GREATER:
    	case GREATER_EQUAL:
    	case LESSER:
    	case LESSER_EQUAL:
    		relop();aux=simpleExpr();
    		if( aux == IdentifierType.INT || aux == IdentifierType.FLOAT)
    			t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
		default:
			break;
    	}
    	return t;
    }
    
    private IdentifierType simpleExpr() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux1;
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		aux = term();
    		aux1 = simpleExprAux();
    		if(aux == IdentifierType.ERROR || aux1 == IdentifierType.ERROR) {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		else if(aux1 == IdentifierType.UNDEFINED)t = aux;
    		else if(aux == aux1) t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType simpleExprAux() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux1;
    	switch(current.type) {
    	case PLUS:
    		addop();aux = term();aux1 = simpleExprAux();
    		if(aux1 == IdentifierType.UNDEFINED) {
    			if(aux == IdentifierType.INT || aux == IdentifierType.STRING || aux == IdentifierType.FLOAT)
    				t = aux;
    			else {
    				t = IdentifierType.ERROR;
        			showSemanticalError(1);
    			}
    		}else if(aux1 == IdentifierType.INT || aux1 == IdentifierType.STRING || aux1 == IdentifierType.FLOAT){
    			if(aux == aux1)
    				t = aux;
    			else {
    				t = IdentifierType.ERROR;
        			showSemanticalError(1);
    			}
    		}else {
				t = IdentifierType.ERROR;   
    			showSemanticalError(1); 			
    		}
    		break;
    	case MINUS:
    		addop();aux = term();aux1 = simpleExprAux();
    		if(aux1 == IdentifierType.UNDEFINED) {
    			if(aux == IdentifierType.INT || aux == IdentifierType.FLOAT)
    				t = aux;
    			else {
    				t = IdentifierType.ERROR;
        			showSemanticalError(1);
    			}
    		}else if(aux1 == IdentifierType.INT || aux1 == IdentifierType.FLOAT){
    			if(aux == aux1)
    				t = aux;
    			else {
    				t = IdentifierType.ERROR;
        			showSemanticalError(1);
    			}
    		}else {
				t = IdentifierType.ERROR; 
    			showSemanticalError(1);   			
    		}
    		break;
    	case OR:
    		addop();aux = term();aux1 = simpleExprAux();
    		if(aux1 == IdentifierType.UNDEFINED) {
    			if(aux == IdentifierType.BOOL)
    				t = aux;
    			else {
    				t = IdentifierType.ERROR;
        			showSemanticalError(1);
    			}
    		}else if(aux1 == IdentifierType.BOOL){
    			if(aux == aux1)
    				t = aux;
    			else {
    				t = IdentifierType.ERROR;
        			showSemanticalError(1);
    			}
    		}else {
				t = IdentifierType.ERROR;  
    			showSemanticalError(1);  			
    		}
    		break;
		default:
			break;
    	}
    	return t;
    }
    
    private IdentifierType term() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux1;
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		aux = factorA();
    		aux1 = termAux();
    		if(aux == IdentifierType.ERROR || aux1 == IdentifierType.ERROR) {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		else if(aux1 == IdentifierType.UNDEFINED)t = aux;
    		else if(aux == aux1) t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType termAux() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux,aux1,aux2;
    	switch(current.type) {
    	case MULT:
    	case DIV:
    	case AND:
    		aux = mulop();aux1 = factorA();aux2 = termAux();
    		if(aux2 == IdentifierType.UNDEFINED) {
    			if(aux == IdentifierType.BOOL) {
        			if( aux1 == IdentifierType.BOOL) 
        				t = IdentifierType.BOOL;
        			else {
        				t = IdentifierType.ERROR;
            			showSemanticalError(1);
        			}
        		}else {
        			if(aux1 == IdentifierType.INT || aux1 == IdentifierType.FLOAT)
        				t = aux1;
        			else {
        				t = IdentifierType.ERROR;
            			showSemanticalError(1);
        			}
        		}
    		}else {
    			if(aux == IdentifierType.BOOL) {
    				if(aux1 == IdentifierType.BOOL && aux2 == IdentifierType.BOOL)
    					t = IdentifierType.BOOL;
    				else {
    					t = IdentifierType.ERROR;
    	    			showSemanticalError(1);
    				}
    					
    			}else {
    				if(aux2 == aux1)
    					t = aux;
    				else {
    					t = IdentifierType.ERROR;
    	    			showSemanticalError(1);
    				}
    			}
    		}
    		break;
		default:
			break;
    	}
    	return t;
    }
    
    private IdentifierType factorA() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	IdentifierType aux;
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    		t = factor();
    		break;
    	case NOT:
    		
    		eat(Tag.NOT);
    		aux = factor();
    		if(aux == IdentifierType.BOOL)t=IdentifierType.BOOL;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
    	case MINUS:
    		eat(Tag.MINUS);
    		aux = factor();
    		if(aux == IdentifierType.INT || aux == IdentifierType.FLOAT) t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
		default:
			showError();
    	}
    	return t;
    }
    private IdentifierType factor() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
		case IDENTIFIER:
			t = getIdType(current.token);
			if(t == IdentifierType.UNDEFINED) {
				t = IdentifierType.ERROR;
				showSemanticalError(2);
			}
			eat(Tag.IDENTIFIER);
			break;
		case INT_C:
		case FLOAT_C:
		case STRING_C:
			t=constant();
			break;
		case OPEN_PAR:
			eat(Tag.OPEN_PAR);
			t=expression();
			eat(Tag.CLOSE_PAR);
			break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType relop() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
    	case EQUAL:
    		t = IdentifierType.BOOL;
    		eat(Tag.EQUAL);
    		break;
    	case GREATER:
    		t = IdentifierType.BOOL;
    		eat(Tag.GREATER);
    		break;
    	case GREATER_EQUAL:
    		t = IdentifierType.BOOL;
    		eat(Tag.GREATER_EQUAL);
    		break;
    	case LESSER:
    		t = IdentifierType.BOOL;
    		eat(Tag.LESSER);
    		break;
    	case LESSER_EQUAL:
    		t = IdentifierType.BOOL;
    		eat(Tag.LESSER_EQUAL);
    		break;
    	case DIFF:
    		t = IdentifierType.BOOL;
    		eat(Tag.DIFF);
    		break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType addop() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
    	case PLUS:
    		eat(Tag.PLUS);
    		break;
    	case MINUS:
    		eat(Tag.MINUS);
    		break;
    	case OR:
    		t = IdentifierType.BOOL;
    		eat(Tag.OR);
    		break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType mulop() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
    	case MULT:
    		eat(Tag.MULT);
    		break;
    	case DIV:
    		eat(Tag.DIV);
    		break;
    	case AND:
    		t = IdentifierType.BOOL;
    		eat(Tag.AND);
    		break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType constant() throws IOException {
    	IdentifierType t = IdentifierType.UNDEFINED;
    	switch(current.type) {
    	case INT_C:
    		eat(Tag.INT_C);
    		t = IdentifierType.INT;
    		break;
    	case FLOAT_C:
    		eat(Tag.FLOAT_C);
    		t = IdentifierType.FLOAT;
    		break;
    	case STRING_C:
    		eat(Tag.STRING_C);
    		t = IdentifierType.STRING;
    		break;
		default:
			showError();
    	}
    	return t;
    }
    
    private IdentifierType getIdType(String token) {
    	return this.lex.st.find(token).getType();
    }
    
    private void setIdType(String token,IdentifierType t) {
    	this.lex.st.find(token).setType(t);
    }
    
    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema invalido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema nao esperado [%s]%s\n", current.token,current.type.name());
                break;
        }

        System.exit(1);
    }
    
    private void showSemanticalError(int error) {
        System.out.printf("%02d: ", lex.getLine());

        switch (error) {
            case 0:
                System.out.printf("Redefinicao de variavel \n");
                break;
            case 1:
                System.out.printf("Tipos Incompativeis \n");
                break;
            case 2:
                System.out.printf("Variavel nao declarada \n");
                break;
            default:
                System.out.printf("Lexema nao esperado [%s]%s\n", current.token,current.type.name());
                break;
        }

        System.exit(1);
    }
}
