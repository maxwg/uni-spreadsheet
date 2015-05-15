package expressions;

import helpers.Tokenizer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import data.CellIndex;
import data.WorkSheet;
import dataStructures.ListStreamReverse;
import dataStructures.Stream;
import expressions.functions.Funclass;

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
		}
		if (this instanceof Brackets) {
			for (Expression expr : ((Brackets) this).expressions)
				refCells.addAll(expr.getReferencedCells());
		} else {
			for (Field f : this.getClass().getDeclaredFields()) {
				if (f.getType().equals(Expression.class))
					for (CellIndex c : ((Expression) f.get(this))
							.getReferencedCells())
						refCells.add(c);
			}
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
	public static ArrayList<Funclass> funs;
	public static void refreshFuns() {
		funs.addAll(Arrays.asList(new Funclass(Sin.class), new Funclass(
				Cos.class), new Funclass(Ln.class), new Funclass(Log.class)));
	}

	private static Class<Const>[] consts = new Class[] { Pi.class, E.class };
	private static WorkSheet worksheet;

	private static Expression parseBinaryExp(Stream tokens,
			Class<BinaryOp>[] lexp, String nextMethod) throws Exception {
		Method nextParse = Expression.class.getDeclaredMethod(nextMethod,
				Stream.class);
		Expression exp1 = (Expression) nextParse.invoke(null, tokens);
		for (Class<BinaryOp> binop : lexp) {
			BinaryOp inst = binop.newInstance();
			if (inst.getToken().equals(tokens.current()) && !tokens.hasEnded()) {
				tokens.next();
				Expression exp2 = parseBinaryExp(tokens, lexp, nextMethod);
				return (Expression) binop.getConstructor(Expression.class,
						Expression.class).newInstance(exp2, exp1);
			}

		}
		return exp1;
	}

	private static Expression parseAEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, aexp, "parseMEXP");
	}

	private static Expression parseMEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, mexp, "parseDEXP");
	}

	private static Expression parseDEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, dexp, "implicitOperations");
	}

	private static Expression implicitOperations(Stream tokens)
			throws Exception {
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

	private static Expression parsePEXP(Stream tokens) throws Exception {
		return parseBinaryExp(tokens, pexp, "parseFEXP");
	}

	private static Expression parseFEXP(Stream tokens) throws Exception {
		Expression exp1 = parseVEXP(tokens);
		if (!tokens.hasEnded() && exp1 instanceof Brackets) {
			for (Funclass fn : funs) {
				FunctionOp inst = fn.newInstance();
				if ((tokens.current() instanceof String)
						&& ((String) tokens.current()).toUpperCase().equals(
								inst.getToken())) {
					if (inst.noParameters() != 0
							&& inst.noParameters() != ((Brackets) exp1).size()) {
						throw new ParseException("Function " + inst.getToken()
								+ " expects " + inst.noParameters()
								+ "parameters. " + ((Brackets) exp1).size()
								+ " provided");
					}
					tokens.next();
					return (Expression) fn.newInstance((Brackets)exp1);
				}
			}
		}
		return exp1;
	}

	private static Expression parseVEXP(Stream tokens) throws Exception {
		if (tokens.hasEnded())
			throw new ParseException("Token stream ended unexpectedly");
		// Parse Brackets
		if (")".equals(tokens.current())) {
			ArrayList<Expression> args = new ArrayList<Expression>();
			tokens.next();
			args.add(parseAEXP(tokens));
			while (",".equals(tokens.current())) {
				tokens.next();
				args.add(parseAEXP(tokens));
			}
			tokens.validateCurrent("(");
			return new Brackets(args.toArray(new Expression[args.size()]));
		}
		// Parse Constant values (pi, e, etc)
		for (Class<Const> inst : consts) {
			Const con = inst.newInstance();
			if (tokens.current() instanceof String
					&& con.getToken().equals(
							((String) tokens.current()).toUpperCase())) {
				tokens.next();
				return (Expression) con;
			}
		}
		// Parse Numbers and CellRefs
		Expression exp = null;
		try {
			exp = new Number((double) tokens.current());
		} catch (ClassCastException e) {
			try {
				exp = new CellIndex(((String) tokens.current()).toUpperCase(),
						worksheet);
			} catch (ClassCastException | NumberFormatException
					| IndexOutOfBoundsException ex) {
				throw new ParseException(tokens.current()
						+ " is not a valid value expression.");
			}
		}
		tokens.next();
		return exp;
	}

	public static Expression calculate(String expr, WorkSheet ws)
			throws Exception {
		worksheet = ws;
		ArrayList<Object> tokens = Tokenizer.tokenize(expr);
		try {
			return parseAEXP(new ListStreamReverse(tokens));
		} catch (InstantiationException | IllegalAccessException
				| InvocationTargetException | IllegalArgumentException
				| NoSuchMethodException | SecurityException | ParseException e) {
			//e.printStackTrace();
			return null;
		}
	}
}
