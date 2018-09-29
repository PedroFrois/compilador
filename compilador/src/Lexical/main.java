package Lexical;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 1) {
            System.out.println("Usage: java compiler [Path File]");
            return;
        }

        try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
            


            // O código a seguir é usado apenas para testar o analisador léxico.
            // TODO: depois de pronto, comentar o código abaixo.
            Lexeme lex = l.nextToken();
            while (checkType(lex.type)) {
                System.out.printf("(\"%s\", %s)\n", lex.token, lex.type);
                lex = l.nextToken();
            }

            switch (lex.type) {
                case INVALID_TOKEN:
                    System.out.printf("%02d: Lexema inválido [%s]\n", l.getLine(), lex.token);
                    break;
                case UNEXPECTED_EOF:
                    System.out.printf("%02d: Fim de arquivo inesperado\n", l.getLine());
                    break;
                default:
                    System.out.printf("(\"%s\", %s)\n", lex.token, lex.type);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
        }
    }

    private static boolean checkType(Tag type) {
        return !(type == Tag.END_OF_FILE ||
                 type == Tag.INVALID_TOKEN ||
                 type == Tag.UNEXPECTED_EOF);
    }
	

}
