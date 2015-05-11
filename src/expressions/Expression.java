package expressions;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import data.CellIndex;
import data.WorkSheet;
import dataStructures.ListStreamReverse;
import dataStructures.Stream;

/**
 * @author max
 * 
 * @grammar The grammar used in these expressions is a context free, left
 *          associate unambiguous grammar which should possess traditional
 *          mathematical precedence rules. The grammar is as follows:
 */
// aexp ::= mexp <AdditiveBinaryOp> aexp | mexp
// mexp ::= dexp <MultiplicativeBinaryOp> mexp | dexp
// dexp ::= iexp <DivisiveBinaryOp> dexp | iexp
// iexp ::= pexp (ImpliedMult) iexp | <Neg> pexp
// pexp ::= fexp <ExponentialBinaryOp> pexp | fexp
// fexp ::= <FunctionOp>(aexp[]) | vexp
// vexp ::= (aexp) | <Number> | <Const> | <CellRef>
@SuppressWarnings("unchecked")
public abstract class Expression {
	public abstract String show();

	public abstract double evaluate();

	public abstract String toLatex();

	public List<CellIndex> getReferencedCells()
			throws IllegalArgumentException, IllegalAccessException {
		List<CellIndex> refCells = new ArrayList<CellIndex>();
		if (this instanceof CellIndex) {
			refCells.add((CellIndex) this);
		} else {
			for (Field f : this.getClass().getDeclaredFields())
				if (f.getType().equals(Expression.class))
					for (CellIndex c : ((Expression) f.get(this))
							.getReferencedCells())
						refCells.add(c);
		}
		return refCells;
	}

	public List<Expression> getInnerExpressions()
			throws IllegalArgumentException, IllegalAccessException {
		List<Expression> innerExpressions = new ArrayList<Expression>();
		for (Field f : this.getClass().getDeclaredFields())
			if (f.getType().equals(Expression.class))
				innerExpressions.add((Expression) f.get(this));
		return innerExpressions;
	}

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
			operatorSymbols.add("(");
			operatorSymbols.add(",");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	private static Class<FunctionOp>[] fexp = new Class[] { Sin.class,
			Cos.class, Ln.class, Log.class };
	private static Class<Const>[] consts = new Class[] { Pi.class, E.class };
	private static WorkSheet worksheet;

	private static Expression parseBinaryExp(Stream tokens,
			Class<BinaryOp>[] lexp, Method nextParse,
			Method additionalProcessing) throws Exception {
		Expression exp1 = (Expression) nextParse.invoke(null, tokens);
		for (Class<BinaryOp> binop : lexp) {
			BinaryOp inst = binop.newInstance();
			if (inst.getToken().equals(tokens.current()) && !tokens.hasEnded()) {
				tokens.next();
				Expression exp2 = parseBinaryExp(tokens, lexp, nextParse,
						additionalProcessing);
				return (Expression) binop.getConstructor(Expression.class,
						Expression.class).newInstance(exp2, exp1);
			}

		}
		return exp1;
	}

	public static void doNothing(Expression prev, Stream tokens) {
	}

	public static Expression parseAEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, aexp, (Expression.class.getMethod(
				"parseMEXP", Stream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, Stream.class)));
	}

	public static Expression parseMEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, mexp, (Expression.class.getMethod(
				"parseDEXP", Stream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, Stream.class)));
	}

	public static Expression parseDEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, dexp, (Expression.class.getMethod(
				"implicitOperations", Stream.class)), (Expression.class
				.getMethod("doNothing", Expression.class, Stream.class)));
	}

	public static Expression implicitOperations(Stream tokens) throws Exception {
		Expression exp1 = parsePEXP(tokens);
		if (!tokens.hasEnded())
			if (!operatorSymbols.contains(tokens.current())) {
				Expression exp2 = implicitOperations(tokens);
				return new Mult(exp2, exp1);
			} else if ("-".equals(tokens.current())
					&& (!tokens.hasNext() || operatorSymbols.contains(tokens
							.peekNext()))) {
				tokens.next();
				return new Neg(exp1);
			}
		return exp1;
	}

	public static Expression parsePEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, pexp, (Expression.class.getMethod(
				"parseFEXP", Stream.class)), (Expression.class.getMethod(
				"doNothing", Expression.class, Stream.class)));
	}

	// public static Expression parseFEXP(Stream tokens) throws Exception {
	// for (Class<FunctionOp> unop : fexp) {
	// FunctionOp inst = unop.newInstance();
	// if ((tokens.current() instanceof String)
	// && ((String) tokens.current()).toUpperCase().equals(
	// inst.getToken())) {
	// if (!")".equals(tokens.next()))
	// throw new ParseException();
	// ArrayList<Expression> exprs = new ArrayList<Expression>();
	// int pars = inst.noParameters();
	// tokens.next();
	// for (; pars > 1; pars--) {
	// exprs.add(parseAEXP(tokens));
	// validateCurrent(tokens, ",");
	// }
	// exprs.add(parseAEXP(tokens));
	// validateCurrent(tokens, "(");
	// Class<Expression>[] expclass = new Class[exprs.size()];
	// for (int i = 0; i < exprs.size(); i++)
	// expclass[i] = Expression.class;
	// return (Expression) unop.getConstructor(expclass).newInstance(
	// exprs.toArray());
	// }
	// }
	// return parseVEXP(tokens);
	// }

	public static Expression parseFEXP(Stream tokens) throws Exception {
		Expression exp1 = parseVEXP(tokens);
		if (!tokens.hasEnded() && exp1 instanceof Brackets) {
			for (Class<FunctionOp> unop : fexp) {
				FunctionOp inst = unop.newInstance();
				if ((tokens.current() instanceof String)
						&& ((String) tokens.current()).toUpperCase().equals(
								inst.getToken())) {
					if(inst.noParameters() != 0 && inst.noParameters() != ((Brackets)exp1).size()){
						System.out.println(inst.noParameters() + "  " + ((Brackets)exp1).size());
						throw new ParseException();
					}
					tokens.next();
					return (Expression) unop.getConstructor(Brackets.class)
							.newInstance((Brackets)exp1);
				}
			}
		}
		return exp1;
	}

	public static Expression parseVEXP(Stream tokens) throws Exception {
		if (tokens.hasEnded())
			throw new ParseException();
		if (")".equals(tokens.current())) {
			ArrayList<Expression> args = new ArrayList<Expression>();
			tokens.next();
			args.add(parseAEXP(tokens));
			while(",".equals(tokens.current())){
				tokens.next();
				args.add(parseAEXP(tokens));
			}
			validateCurrent(tokens, "(");
			return new Brackets(args.toArray(new Expression[args.size()]));
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
				//System.err.println(tokens.current() + " is not a valid value token!");
				throw new ParseException();
			}
		}
		tokens.next();
		return exp;
	}

	public static void validateCurrent(Stream tokens, String val)
			throws ParseException {
		if (!val.equals(tokens.current())) {
			throw new ParseException();
		}
		tokens.next();
	}

	public static Expression calculate(String expr, WorkSheet ws)
			throws Exception {
		worksheet = ws;
		StreamTokenizer tok = new StreamTokenizer(new StringReader(expr));
		tok.ordinaryChar('/');
		ArrayList<Object> tokens = new ArrayList<Object>();
		int tVal;
		while ((tVal = tok.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tVal) {
			case StreamTokenizer.TT_NUMBER:
				Object ptoken = tokens.size() > 0 ? tokens
						.get(tokens.size() - 1) : null;
				if (tok.nval < 0
						&& (ptoken instanceof Double || ")".equals(ptoken))) {
					tokens.add("-");
					tokens.add(-tok.nval);
				} else
					tokens.add(new Double(tok.nval));
				break;
			case StreamTokenizer.TT_WORD:
				String[] s = tok.sval.split("-");
				for (int i = 0; i < s.length; i++) {
					try {
						tokens.add(Double.parseDouble(s[i]));
					} catch (NumberFormatException e) {
						tokens.add(s[i]);
					}
					if (i < s.length - 1)
						tokens.add("-");
				}
				break;
			default:
				tokens.add(Character.toString((char) tVal));
				break;
			}
		}
		try {
			return parseAEXP(new ListStreamReverse(tokens));
		} catch (InstantiationException | IllegalAccessException
				| InvocationTargetException | IllegalArgumentException
				| NoSuchMethodException | SecurityException | ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
