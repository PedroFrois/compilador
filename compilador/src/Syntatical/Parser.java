package Syntatical;

import java.io.IOException;

import Lexical.*;

public class Parser {
	private LexicalAnalysis lex;
    private Lexeme current;
    private String obj;
    private int nextAddress;
    private int indexLabel;
    public Parser(LexicalAnalysis lex) throws IOException {
        this.lex = lex;
        this.current = lex.nextToken();
        obj = "";
        nextAddress=0;
        indexLabel =0;
    }

    public void start() throws IOException {
    	obj+="START\n";
    	program();
    	obj+="STOP\n";
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
    		eat(Tag.START);declList();	
    		stmtList();eat(Tag.EXIT);eat(Tag.END_OF_FILE);
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
    		switch(t) {
			case INT:
				obj+="PUSHN 1\n";
				break;
			case FLOAT:
				obj+="PUSHF 0.0\n";
				break;
			case STRING:
				obj+="PUSHS \"\"\n";
				break;
			default:
				showSemanticalError(1);
		}
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
    		switch(t) {
				case INT:
					obj+="PUSHN 1\n";
					break;
				case FLOAT:
					obj+="PUSHF 0.0\n";
					break;
				case STRING:
					obj+="PUSHS \"\"\n";
					break;
				default:
					showSemanticalError(1);
			}
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
    	int address;
    	switch(current.type) {
    	case IDENTIFIER:
    		address= getIdAddress(current.token);
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
    		obj += "STOREL "+address+"\n";
    		break;
    	default:
    		showError();
    	}
    	return t;
    }
    
    private void ifStmt() throws IOException {
    	String elseLabel = "LABEL"+this.indexLabel;
    	this.indexLabel++;
    	String endLabel = "LABEL"+this.indexLabel;
    	this.indexLabel++;
    	switch(current.type) {
    	case IF:
    		eat(Tag.IF);condition();
    		obj += "JZ "+elseLabel+"\n";
    		eat(Tag.THEN);stmtList();
    		obj+= "JUMP "+endLabel+"\n";
    		endIfStmt(elseLabel,endLabel);
    		break;
    	default:
    		showError();
    	}
    }
    
    private void endIfStmt(String elseLabel,String endLabel) throws IOException {

		obj += elseLabel+":";
    	switch(current.type) {
    	case END:
    		obj += "NOP\n";//não tem else
    		eat(Tag.END);
    		break;
    	case ELSE:
    		eat(Tag.ELSE);stmtList();eat(Tag.END);
    		break;
    	default:
    		showError();
    	}
		obj += endLabel+":";
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
    	String label = "LABEL" + this.indexLabel;
    	indexLabel++;
    	switch(current.type) {
    	case DO:
    		obj+=label+":";
    		eat(Tag.DO);stmtList();whileSuffix(label);
    		break;
		default:
			showError();
    	}
    }
    
    private void whileSuffix(String label) throws IOException {
    	switch(current.type) {
    	case WHILE:
    		eat(Tag.WHILE);condition();

    		obj += "NOT\n";
    		obj+="JZ "+label+"\n";//jz pula quando falso
    		eat(Tag.END);
    		break;
		default:
			showError();
    	}
    }
    
    private void readStmt() throws IOException {
    	IdentifierType aux;
    	int address;
    	switch(current.type) {
    	case SCAN:
    		eat(Tag.SCAN);eat(Tag.OPEN_PAR);
    		aux = getIdType(current.token);
    		address = getIdAddress(current.token);
    		eat(Tag.IDENTIFIER);eat(Tag.CLOSE_PAR);
    		obj+="READ\n";
    		if(aux == IdentifierType.UNDEFINED) {
    			showSemanticalError(2);
    		}else if(aux == IdentifierType.INT){
    			obj += "ATOI\n";
    		}else if(aux == IdentifierType.FLOAT) {
    			obj += "ATOF\n";
    		}
    		obj += "STOREL "+address+"\n";
    		break;
		default:
			showError();
    	}
    }
    
    private void writeStmt() throws IOException {
    	switch(current.type) {
    	case PRINT:
    		eat(Tag.PRINT);eat(Tag.OPEN_PAR);
    		switch(writable()) {
	    		case INT:
	    			obj+= "WRITEI\n";
	    			break;
	    		case STRING:
    				obj+= "WRITES\n";
	    			break;
	    		case FLOAT:
	    			obj+= "WRITEF\n";
	    			break;
    			default:
        			showSemanticalError(1);
	    				
    		};
    		eat(Tag.CLOSE_PAR);
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
    		relop();aux=simpleExpr();
    		if(aux == IdentifierType.STRING || aux == IdentifierType.INT || aux == IdentifierType.FLOAT)
    			t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		obj+="EQUAL\n";
    		break;
    		
    	case DIFF:
    		relop();aux=simpleExpr();
    		if(aux == IdentifierType.STRING || aux == IdentifierType.INT || aux == IdentifierType.FLOAT)
    			t = aux;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		obj+="EQUAL\n";
    		obj+="NOT\n";
    		break;
    		
    	case GREATER:
    		relop();aux=simpleExpr();
    		if( aux == IdentifierType.INT) {
    			t = aux;
    			obj += "SUP\n";
    		}else if(aux == IdentifierType.FLOAT) {
    			t = aux;
    			obj += "FSUP\n";
    		}else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		
    		
    		break;
    		
    	case GREATER_EQUAL:
    		relop();aux=simpleExpr();
    		if( aux == IdentifierType.INT) {
    			t = aux;
    			obj += "SUPEQ\n";
    		}else if(aux == IdentifierType.FLOAT) {
    			t = aux;
    			obj += "FSUPEQ\n";
    		}else {
    			t = IdentifierType.ERROR;
    		}
    		break;
    		
    	case LESSER:
    		relop();aux=simpleExpr();
    		if( aux == IdentifierType.INT) {
    			t = aux;
    			obj += "INF\n";
    		}else if(aux == IdentifierType.FLOAT) {
    			t = aux;
    			obj += "FINF\n";
    		}else {
    			t = IdentifierType.ERROR;
    		}
    		break;
    		
    	case LESSER_EQUAL:
    		relop();aux=simpleExpr();
    		if( aux == IdentifierType.INT) {
    			t = aux;
    			obj += "INFEQ\n";
    		}else if(aux == IdentifierType.FLOAT) {
    			t = aux;
    			obj += "FINFEQ\n";
    		}else {
    			t = IdentifierType.ERROR;
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
    		switch(t) {
	    		case STRING:
	    			obj+="SWAP\n";
	    			obj+="CONCAT\n";
	    			break;
	    		case INT:
	    			obj+="ADD\n";
	    			break;
	    		case FLOAT:
	    			obj+="FADD\n";
	    			break;
    			default:
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

    		switch(t) {
	    		case INT:
	    			obj+="SUB\n";
	    			break;
	    		case FLOAT:
	    			obj+="FSUB\n";
	    			break;
	    		default:
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
    		obj += "ADD\n";
    		obj += "PUSHI 0\n";
    		obj += "SUP\n";
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
    		
    		switch(t) {
	    		case INT:
	    			obj+="MUL\n";
	    			break;
	    		case FLOAT:
	    			obj+="FMUL\n";
	    			break;
    			default:
    				showSemanticalError(1);
    		}
    		break;
    		
    	case DIV:
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
    		
    		switch(t) {
				case INT:
					obj+="DIV\n";
					break;
				case FLOAT:
					obj+="FDIV\n";
					break;
				default:
					showSemanticalError(1);	
    		}
    		break;
    		
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
    		
    		obj += "MUL\n";
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
    		obj+="NOT\n";
    		if(aux == IdentifierType.BOOL)t=IdentifierType.BOOL;
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		break;
    	case MINUS:
    		eat(Tag.MINUS);
    		aux = factor();
    		if(aux == IdentifierType.INT) {
        		obj += "PUSHI 0\n";
    		}else if(aux == IdentifierType.FLOAT) {
    			t = aux;
        		obj += "PUSHI 0.0\n";
    		}
    		else {
    			t = IdentifierType.ERROR;
    			showSemanticalError(1);
    		}
    		obj += "SWAP\n";
    		obj += "MINUS\n";
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
			obj+= "PUSHL "+ getIdAddress(current.token)+"\n";
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
    		obj+="PUSHI "+current.token+"\n";
    		eat(Tag.INT_C);
    		t = IdentifierType.INT;
    		break;
    	case FLOAT_C:
    		obj+="PUSHF "+current.token+"\n";
    		eat(Tag.FLOAT_C);
    		t = IdentifierType.FLOAT;
    		break;
    	case STRING_C:
    		obj+="PUSHS \""+current.token+"\"\n";
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
    
    
    }private int getIdAddress(String token) {
    	return this.lex.st.find(token).getAddress();
    }
    
    private void setIdType(String token,IdentifierType t) {
    	SymbolTableElement x = this.lex.st.find(token);
    	x.setType(t);
    	x.setAddress(this.nextAddress);
    	this.nextAddress++;
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
    
    public String getObj() {
    	return obj;
    }
}
