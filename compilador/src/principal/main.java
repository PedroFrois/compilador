package principal;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import Lexical.LexicalAnalysis;
import Lexical.LexicalException;
import Lexical.Tag;
import Syntatical.Parser;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2) {
			System.out.println("Usage: java compiler [Path File] [Path to Obj] ");
			return;
		}
		try {
			Parser p = new Parser(new LexicalAnalysis(args[0]));
			p.start();
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf-8"));
			writer.write(p.getObj());
			writer.close();
		} catch (IOException | LexicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
			int i = 0;
			// O codigo a seguir eh usado apenas para testar o analisador lexico.
			// TODO: depois de pronto, comentar o codigo abaixo.
			Lexeme lex = l.nextToken();
			while (checkType(lex.type)) {
				System.out.printf("%02d_(\"%s\", %s)\n", i++, lex.token, lex.type);
				lex = l.nextToken();
			}

			switch (lex.type) {
			case INVALID_TOKEN:
				System.out.printf("%02d_%02d: Lexema invalido [%s]\n", i++, l.getLine(), lex.token);
				break;
			case UNEXPECTED_EOF:
				System.out.printf("%02d_%02d: Fim de arquivo inesperado\n", i++, l.getLine());
				break;
			default:
				System.out.printf("%02d_(\"%s\", %s)\n", i++, lex.token, lex.type);
				break;
			}
			l.printTable();
		} catch (Exception e) {
			System.err.println("Internal error: " + e.getMessage());
		}*/
	}

	private static boolean checkType(Tag type) {
		return !(type == Tag.END_OF_FILE || type == Tag.INVALID_TOKEN || type == Tag.UNEXPECTED_EOF);
	}

}
