package expressions;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
 *          MAY NEED MULTIPLICATION DIVISION PRECEDENCE!
 */
// aexp ::= mexp <AdditiveBinaryOp> aexp | mexp
// mexp ::= dexp <MultiplicativeBinaryOp> mexp | dexp
// dexp ::= pexp <DivisiveBinaryOp> dexp | fexp (ImpliedMult) dexp | pexp
// pexp ::= fexp <ExponentialBinaryOp> pexp | fexp
// fexp ::= <FunctionOp>(aexp[]) | vexp
// vexp ::= (aexp) | <Number> | <Const> | <CellRef>
@SuppressWarnings("unchecked")
public abstract class Expression {
	public abstract String show();

	public abstract double evaluate();

	public abstract String toLatex();
	
	public abstract List<CellIndex> getReferencedCells();

	private static Class<BinaryOp>[] aexp = new Class[] { Add.class, Sub.class };
	private static Class<BinaryOp>[] dexp = new Class[] { Div.class };
	private static Class<BinaryOp>[] mexp = new Class[] { Mult.class };
	private static Class<BinaryOp>[] pexp = new Class[] { Pow.class };
	private static ArrayList<String> operatorSymbols;
	static {
		operatorSymbols = new ArrayList<String>();
		try {
			for (Class<BinaryOp> inst : aexp)
				operatorSymbols.add(inst.newInstance().getToken());
			for (Class<BinaryOp> inst : dexp)
				operatorSymbols.add(inst.newInstance().getToken());
			for (Class<BinaryOp> inst : mexp)
				operatorSymbols.add(inst.newInstance().getToken());
			for (Class<BinaryOp> inst : pexp)
				operatorSymbols.add(inst.newInstance().getToken());
			operatorSymbols.add(")");
			operatorSymbols.add(",");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	private static Class<FunctionOp>[] fexp = new Class[] { Sin.class,
			Cos.class, Ln.class, Log.class };
	private static Class<Const>[] consts = new Class[] { Pi.class };
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
				"parseDEXP", ListStream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, ListStream.class)));
	}

	public static Expression parseDEXP(ListStream tokens) throws Exception {
		return parseBinaryExp(tokens, dexp, (Expression.class.getMethod(
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
				if (impliedWithBrackets){
					validateCurrent(tokens, ")");
					return new Brackets(new Mult(exp1, exp2));
				}
				
				return new Mult(exp1, exp2);
			}
		}
		return null;
	}

	public static Expression parsePEXP(ListStream tokens) throws Exception {
		return parseBinaryExp(tokens, pexp, (Expression.class.getMethod(
				"parseFEXP", ListStream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, ListStream.class)));
	}

	public static Expression parseFEXP(ListStream tokens) throws Exception {
		for (Class<FunctionOp> unop : fexp) {
			FunctionOp inst = unop.newInstance();
			if ((tokens.current() instanceof String)
					&& ((String) tokens.current()).toUpperCase().equals(
							inst.getToken())) {
				if (!"(".equals(tokens.next()))
					throw new ParseException();
				ArrayList<Expression> exprs = new ArrayList<Expression>();
				int pars = inst.noParameters();
				tokens.next();
				for (; pars > 1; pars--) {
					exprs.add(parseAEXP(tokens));
					validateCurrent(tokens, ",");
				}
				exprs.add(parseAEXP(tokens));
				validateCurrent(tokens, ")");
				Class<Expression>[] expclass = new Class[exprs.size()];
				for (int i = 0; i < exprs.size(); i++)
					expclass[i] = Expression.class;
				return (Expression) unop.getConstructor(expclass).newInstance(
						exprs.toArray());
			}
		}
		return parseVEXP(tokens);
	}

	public static Expression parseVEXP(ListStream tokens) throws Exception {
		if (tokens.hasEnded())
			throw new ParseException();
		if ("(".equals(tokens.current())) {
			tokens.next();
			Expression exp = parseAEXP(tokens);
			validateCurrent(tokens, ")");
			return new Brackets(exp);
		}
		for (Class<Const> inst : consts) {
			Const con = inst.newInstance();
			if (tokens.current() instanceof String
					&& con.getToken().equals(
							((String) tokens.current()).toUpperCase())) {
				tokens.next();
				return (Expression) con;
			}
		}
		Expression exp = null;
		try {
			exp = new Number((double) tokens.current());
		} catch (ClassCastException e) {
			try {
				exp = new CellIndex(((String) tokens.current()).toUpperCase(),
						worksheet);
			} catch (ClassCastException | NumberFormatException
					| IndexOutOfBoundsException ex) {
				throw new ParseException();
			}
		}
		tokens.next();
		return exp;
	}

	public static void validateCurrent(ListStream tokens, String val)
			throws ParseException {
		if (!val.equals(tokens.current())){
			throw new ParseException();
		}
		tokens.next();
	}

	public static Expression calculate(String expr, WorkSheet ws) throws Exception {
		worksheet = ws;
		StreamTokenizer tok = new StreamTokenizer(new StringReader(expr));
		tok.ordinaryChar('/');
		ArrayList<Object> tokens = new ArrayList<Object>();
		int tVal;
		while ((tVal = tok.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tVal) {
			case StreamTokenizer.TT_NUMBER:
				Object ptoken = tokens.size() - 1 > 0 ? tokens.get(tokens.size() - 1) : null;
				if (tok.nval < 0
						&& (ptoken instanceof Double || ")".equals(ptoken))) {
					tokens.add("-");
					tokens.add(-tok.nval);
				} else
					tokens.add(new Double(tok.nval));
				break;
			case StreamTokenizer.TT_WORD:
				String[] s = tok.sval.split("-");
				for(int i = 0; i < s.length; i++){
					try{
						tokens.add(Double.parseDouble(s[i]));
					}catch(NumberFormatException e){
						tokens.add(s[i]);
					}
					if (i <s.length-1)
						tokens.add("-");
				}
				break;
			default:
				tokens.add(Character.toString((char) tVal));
				break;
			}
		}
		try {
			return parseAEXP(new ListStream(tokens));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalArgumentException |  NoSuchMethodException | SecurityException | ParseException e) {
			//e.printStackTrace();
			return null;
		}
	}
}
