package expressions;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author max
 * 
 * @grammar The grammar used in these expressions is a context free, left
 *          associate unambiguous grammar which should possess traditional
 *          mathematical precedence rules. The grammar is as follows:
 * 
 */
// aexp ::= aexp <AdditiveBinaryOp> mexp | mexp
// mexp ::= mexp <MultiplicativeBinaryOp> pexp | pexp
// pexp ::= pexp <ExponentialBinaryOp> fexp | fexp
// fexp ::= <UnaryOp>(aexp[]) | vexp
// vexp ::= (aexp) | <Number> | <CellRef>
public abstract class Expression {
	public abstract String show();

	public abstract double evaluate();

	private static Class[] aexp = new Class[] { Add.class, Sub.class };
	//USE STARTENDINDEX IN PLACE
	public static Expression parse(Object[] tokens) throws IOException {
		if (tokens.length == 1 && tokens[0] instanceof Double)
			return new Number((double) tokens[0]);
		for (int i = 0; i < tokens.length; i++) {
			return null;
		}
		return null;
	}

	public static Double calculate(String expr) throws IOException {
		StreamTokenizer tok = new StreamTokenizer(new StringReader(expr));
		tok.ordinaryChar('/');
		ArrayList<Object> tokens = new ArrayList<Object>();
		int tVal;
		while ((tVal = tok.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tVal) {
			case StreamTokenizer.TT_NUMBER:
				if (tok.nval < 0
						&& tokens.get(tokens.size() - 1) instanceof Double) {
					tokens.add("-");
					tokens.add(-tok.nval);
				} else
					tokens.add(tok.nval);
				break;
			case StreamTokenizer.TT_WORD:
				tokens.add(tok.sval);
				break;
			default:
				tokens.add(Character.toString((char) tVal));
				break;
			}
		}
		for (Object tk : tokens)
			System.out.println(tk);
		Expression expression = parse(tokens.toArray());
		return expression == null ? null : expression.evaluate();
	}

}
