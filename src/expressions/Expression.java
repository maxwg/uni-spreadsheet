package expressions;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * @author max
 * 
 * @grammar The grammar used in these expressions is a context free, left
 *          associate unambiguous grammar which should possess traditional
 *          mathematical precedence rules. The grammar is as follows:
 * 
 *          aexp ::= aexp + mexp | aexp - mexp | mexp
 *          mexp ::= mexp * pexp | mexp / pexp | pext
 *          pexp ::= pexp ^ fexp | fexp
 *          fexp ::= func(aexp[]) | vexp
 *          vexp ::= (aexp) | number
 */
public abstract class Expression {
	public abstract String show();
	public abstract double evaluate();
	private static Class[] aexp = new Class[]{Add.class, Sub.class};
	public static Expression parse(){
		return null;
	}
	public static double calculate(String expr) throws IOException{
		StreamTokenizer tok = new StreamTokenizer(new StringReader(expr));
		while(tok.nextToken() != StreamTokenizer.TT_EOF){
			System.out.println(tok.toString());
		}
		return 43;
	}
	
}
