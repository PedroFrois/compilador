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
    private void decl() throws IOException {
    	switch(current.type) {
    	case INT_T:
    	case FLOAT_T:
    	case STRING_T:
    		type();identList();eat(Tag.SEMICOLON);
    		break;
    	default:
    		showError();
    	}
    }
    
    private void identList() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    		eat(Tag.IDENTIFIER);identListAux();
    		break;
		default:
			showError();
    	}
    }
    
    private void identListAux() throws IOException {
    	switch(current.type) {
    	case COMMA:
    		eat(Tag.COMMA);eat(Tag.IDENTIFIER);
    		break;
		default:
			return;
    	}
    }
    
    private void type() throws IOException {
    	switch(current.type) {
    	case INT_T:
    		eat(Tag.INT_T);
    		break;
    	case FLOAT_T:
    		eat(Tag.FLOAT_T);
    		break;
    	case STRING_T:
    		eat(Tag.STRING_T);
    		break;
    	default:
    		showError();
    	}
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
    		assignStmt();eat(Tag.COMMA);
    		break;
    	case IF:
    		ifStmt();
    		break;
    	case DO:
    		whileStmt();
    		break;
    	case SCAN:
    		readStmt();eat(Tag.COMMA);
    		break;
    	case PRINT:
    		writeStmt();eat(Tag.COMMA);
    		break;
    	default:
    		showError();
    	}
    }
    
    private void assignStmt() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    		eat(Tag.IDENTIFIER);eat(Tag.ASSIGN);simpleExpr();
    		break;
    	default:
    		showError();
    	}
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
    		eat(Tag.ELSE);stmtList();eat(Tag.END);stmtList();endIfStmt();
    		break;
    	default:
    		showError();
    	}
    }
    private void condition() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		expression();
    		break;
    	default:
    		showError();
    	}
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
    	switch(current.type) {
    	case SCAN:
    		eat(Tag.SCAN);eat(Tag.OPEN_PAR);eat(Tag.IDENTIFIER);eat(Tag.CLOSE_PAR);
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
    
    private void writable() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		simpleExpr();
    		break;
    	default:
    		showError();
    	}
        
    }
    
    private void expression() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		simpleExpr();expressionAux();
    		break;
		default:
			showError();
    	}
    }
    private void expressionAux() throws IOException {
    	switch(current.type) {
    	case EQUAL:
    	case GREATER:
    	case GREATER_EQUAL:
    	case LESSER:
    	case LESSER_EQUAL:
    	case DIFF:
    		relop();simpleExpr();
    		break;
		default:
			return;
    	}
    }
    private void simpleExpr() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		term();simpleExprAux();
    		break;
		default:
			showError();
    	}
    }
    private void simpleExprAux() throws IOException {
    	switch(current.type) {
    	case PLUS:
    	case MINUS:
    	case OR:
    		addop();term();simpleExprAux();
    		break;
		default:
			return;
    	}
    }
    
    private void term() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    	case NOT:
    	case MINUS:
    		factorA();termAux();
    		break;
		default:
			showError();
    	}
    }
    
    private void termAux() throws IOException {
    	switch(current.type) {
    	case MULT:
    	case DIV:
    	case AND:
    		mulop();factorA();termAux();
    		break;
		default:
			return;
    	}
    }
    
    private void factorA() throws IOException {
    	switch(current.type) {
    	case IDENTIFIER:
    	case INT_C:
    	case FLOAT_C:
    	case STRING_C:
    	case OPEN_PAR:
    		factor();
    		break;
    	case NOT:
    		eat(Tag.NOT);factor();
    		break;
    	case MINUS:
    		eat(Tag.MINUS);factor();
    		break;
		default:
			showError();
    	}
    }
    private void factor() throws IOException {
    	switch(current.type) {
		case IDENTIFIER:
			eat(Tag.IDENTIFIER);
			break;
		case INT_C:
		case FLOAT_C:
		case STRING_C:
			constant();
			break;
		case OPEN_PAR:
			eat(Tag.OPEN_PAR);expression();eat(Tag.CLOSE_PAR);
			break;
		default:
			showError();
    	}
    }
    
    private void relop() throws IOException {
    	switch(current.type) {
    	case EQUAL:
    		eat(Tag.EQUAL);
    		break;
    	case GREATER:
    		eat(Tag.GREATER);
    		break;
    	case GREATER_EQUAL:
    		eat(Tag.GREATER_EQUAL);
    		break;
    	case LESSER:
    		eat(Tag.LESSER);
    		break;
    	case LESSER_EQUAL:
    		eat(Tag.LESSER_EQUAL);
    		break;
    	case DIFF:
    		eat(Tag.DIFF);
    		break;
		default:
			showError();
    	}
    }
    
    private void addop() throws IOException {
    	switch(current.type) {
    	case PLUS:
    		eat(Tag.EQUAL);
    		break;
    	case MINUS:
    		eat(Tag.MINUS);
    		break;
    	case OR:
    		eat(Tag.OR);
    		break;
		default:
			showError();
    	}
    }
    
    private void mulop() throws IOException {
    	switch(current.type) {
    	case MULT:
    		eat(Tag.MULT);
    		break;
    	case DIV:
    		eat(Tag.DIV);
    		break;
    	case AND:
    		eat(Tag.AND);
    		break;
		default:
			showError();
    	}
    }
    
    private void constant() throws IOException {
    	switch(current.type) {
    	case INT_C:
    		eat(Tag.INT_C);
    		break;
    	case FLOAT_C:
    		eat(Tag.FLOAT_C);
    		break;
    	case STRING_C:
    		eat(Tag.STRING_C);
    		break;
		default:
			showError();
    	}
    }
    
    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }
}
