package helpers;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

public class Tokenizer {
	public static ArrayList<Object> tokenize(String expr) throws IOException{
		StreamTokenizer tok = new StreamTokenizer(new StringReader(expr));
		tok.ordinaryChar('/');
		tok.ordinaryChar('-');
		tok.wordChars('#', '#');
		ArrayList<Object> tokens = new ArrayList<Object>();
		int tVal;
		while ((tVal = tok.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tVal) {
			case StreamTokenizer.TT_NUMBER:
					tokens.add(new Double(tok.nval));
				break;
			case StreamTokenizer.TT_WORD:
				tokens.add(tok.sval);
				break;
			default:
				tokens.add(Character.toString((char) tVal));
				break;
			}
		}
		return tokens;
	}
}
