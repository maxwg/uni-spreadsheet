package expressions;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import data.CellIndex;
import data.WorkSheet;
import dataStructures.ListStream;

/**
 * @author max
 * 
 * @grammar The grammar used in these expressions is a context free, left
 *          associate unambiguous grammar which should possess traditional
 *          mathematical precedence rules. The grammar is as follows:
 * 
 */
// aexp ::= mexp <AdditiveBinaryOp> aexp | mexp
// mexp ::= pexp <MultiplicativeBinaryOp> mexp | pexp (ImpliedMult) mexp |pexp
// pexp ::= uexp <ExponentialBinaryOp> pexp | uexp
// uexp ::= <UnaryOp>(aexp[]) | vexp
// vexp ::= (aexp) | <Number> | <CellRef>
@SuppressWarnings("unchecked")
public abstract class Expression {
	public abstract String show();
	public abstract double evaluate();

	private static Class<BinaryOp>[] aexp = new Class[] { Add.class, Sub.class };
	private static Class<BinaryOp>[] mexp = new Class[] { Mult.class, Div.class };
	private static Class<BinaryOp>[] pexp = new Class[] { Pow.class };
	private static ArrayList<String> operatorSymbols;
	static {
		operatorSymbols = new ArrayList<String>();
		try {
			for (Class<BinaryOp> inst : aexp)
				operatorSymbols.add(inst.newInstance().getToken());
			for (Class<BinaryOp> inst : mexp)
				operatorSymbols.add(inst.newInstance().getToken());
			for (Class<BinaryOp> inst : pexp)
				operatorSymbols.add(inst.newInstance().getToken());
			operatorSymbols.add(")");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	private static Class<UnaryOp>[] uexp = new Class[] { Sin.class };
	private static WorkSheet worksheet;
	
	
	private static Expression parseBinaryExp(ListStream tokens,
			Class<BinaryOp>[] lexp, Method nextParse,
			Method additionalProcessing) throws Exception {
		Expression exp1 = (Expression) nextParse.invoke(null, tokens);
		for (Class<BinaryOp> binop : lexp) {
			BinaryOp inst = binop.newInstance();
			if (inst.getToken().equals(tokens.current())) {
				tokens.next();
				Expression exp2 = parseBinaryExp(tokens, lexp, nextParse,
						additionalProcessing);
				return (Expression) binop.getConstructor(Expression.class,
						Expression.class).newInstance(exp1, exp2);
			}
		}
		Expression additional = (Expression) additionalProcessing.invoke(null,
				exp1, tokens);
		return additional == null ? exp1 : additional;
	}

	public static void doNothing(Expression prev, ListStream tokens) {
	}

	public static Expression parseAEXP(ListStream tokens) throws Exception {
		return parseBinaryExp(tokens, aexp, (Expression.class.getMethod(
				"parseMEXP", ListStream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, ListStream.class)));
	}

	public static Expression parseMEXP(ListStream tokens) throws Exception {
		return parseBinaryExp(tokens, mexp, (Expression.class.getMethod(
				"parsePEXP", ListStream.class)), (Expression.class.getMethod(
				"implicitMultiplication", Expression.class, ListStream.class)));
	}

	public static Expression implicitMultiplication(Expression exp1,
			ListStream tokens) throws Exception {
		if (!tokens.hasEnded()) {
			boolean curInAEXP = false;
			for (String sym : operatorSymbols)
				if (sym.equals(tokens.current()))
					curInAEXP = true;
			if (!curInAEXP) {
				boolean impliedWithBrackets = false;
				if ("(".equals(tokens.current())) {
					impliedWithBrackets = true;
					tokens.next();
				}
				Expression exp2 = parseMEXP(tokens);
				if (impliedWithBrackets)
					if (!")".equals(tokens.current()))
						throw new ParseException();
				tokens.next();
				return new Mult(exp1, exp2);
			}
		}
		return null;
	}

	public static Expression parsePEXP(ListStream tokens) throws Exception {
		return parseBinaryExp(tokens, pexp, (Expression.class.getMethod(
				"parseUEXP", ListStream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, ListStream.class)));
	}

	public static Expression parseUEXP(ListStream tokens) throws Exception {
		for (Class<UnaryOp> unop : uexp) {
			UnaryOp inst = unop.newInstance();
			if ((tokens.current() instanceof String)
					&& ((String) tokens.current()).toUpperCase().equals(
							inst.getToken())) {
				if (!"(".equals(tokens.next()))
					throw new ParseException();
				Expression exp2 = parseAEXP(tokens);
				if (!")".equals(tokens.current()))
					throw new ParseException();
				tokens.next();
				return (Expression) unop.getConstructor(Expression.class)
						.newInstance(exp2);
			}
		}
		return parseVEXP(tokens);
	}

	public static Expression parseVEXP(ListStream tokens) throws Exception {
		if ("(".equals(tokens.current())) {
			tokens.next();
			Expression exp = parseAEXP(tokens);
			if (!")".equals(tokens.current()))
				throw new ParseException();
			tokens.next();
			return exp;
		}
		Expression exp = null;
		try{
			exp = new Number((double) tokens.current());
		}
		catch(ClassCastException e)
		{
			try{
				exp = new CellIndex(((String)tokens.current()).toUpperCase(), worksheet);
			}
			catch(ClassCastException | NumberFormatException | IndexOutOfBoundsException ex){
				throw new ParseException();
			}
		}
		tokens.next();
		return exp;
	}

	public static Double calculate(String expr, WorkSheet ws) throws Exception {
		worksheet = ws;
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
		try {
			return parseAEXP(new ListStream(tokens)).evaluate();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ParseException e) {
			return null;
		}
	}

}
