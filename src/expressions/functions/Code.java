package expressions.functions;

import helpers.Tokenizer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import data.WorkSheet;
import dataStructures.ListStream;
import dataStructures.Stream;
import expressions.Brackets;
import expressions.Expression;
import expressions.ParseException;

/**
 * Executed during running.
 * @author max
 *
 */
public class Code {
	static ArrayList<String> comparators = new ArrayList<String>(Arrays.asList(
			"< =", "> =", "! =", "= =", "<", ">"));
	
	/**
	 * Executes the code itself
	 * @param variables - hashmap of current variables
	 * @param ws - worksheet
	 * @param code - the code itself
	 */
	public static double execute(HashMap<String, Object> variables,
			WorkSheet ws, String code) throws Exception {
		ArrayList<Object> toks = Tokenizer.tokenize(code);
		ListStream tokens = new ListStream(toks);
		while (!tokens.isEnd()) {
			if ("while".equals(tokens.current())) {
				parseWhile(variables, ws, tokens);
			} else if ("if".equals(tokens.current())) {
				parseIf(variables, ws, tokens);
			} else if ("return".equals(tokens.current())) {
				String expr = parseStringExpandVars(variables, ";", tokens, ws);
				return Expression.calculate(expr, ws).evaluate();
			} else {
				if (variables.containsKey(tokens.current())) {
					String var = (String) tokens.current();
					tokens.validateNext("=");
					String expval = parseStringExpandVars(variables, ";",
							tokens, ws);
					variables.put(var, Expression.calculate(expval, ws)
							.evaluate());
					tokens.validateCurrent(";");
				} else {
					String var = (String) tokens.current();
					tokens.validateNext("=");
					String val = parseStringExpandVars(variables, ";", tokens,
							ws);
					Expression value = Expression.calculate(val, ws);
					if(var.toUpperCase().equals(var.toLowerCase()))
						throw new ParseException("Bad variable name - "+var.toString()+". Variable names should contain at least 1 alphabetical character");
					variables.put(var, value.evaluate());
					tokens.validateCurrent(";");
				}
			}
		}
		return 0;
	}

	private static String parseStringExpandVars(
			HashMap<String, Object> variables, String token, Stream tokens,
			WorkSheet ws) throws Exception {
		String expval = "";
		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(10);
		while (!(token.equals(tokens.next())) && !tokens.hasEnded()) {
			if (variables.containsKey(tokens.current())) {
				Object val = variables.get(tokens.current());
				if (val instanceof Double)
					expval += df.format(val) + " ";
				else if (val instanceof Brackets) {
					tokens.validateNext("[");
					String exp = parseStringExpandVars(variables, "]", tokens,
							ws);
					int index = Math.round((float) Expression
							.calculate(exp, ws).evaluate());
					if (index >= ((Brackets) val).size())
						throw new ParseException(
								"Index out of bounds! Array Size "
										+ ((Brackets) val).size()
										+ " index accessed " + index
										+ " at position " + tokens.position());
					//tokens.validateCurrent("]");
					expval += ((Brackets) val).get(index).evaluate() + " ";
				}
			} 
			else {
				expval += tokens.current() + " ";
			}
		}
		return expval;
	}

	private static String parseStringExpandVarsBrackets(
			HashMap<String, Object> variables, Stream tokens, WorkSheet ws)
			throws Exception {
		String expval = "";
		int braceCount = 0;
		while (!(")".equals(tokens.next()) && braceCount == 0)
				&& !tokens.hasEnded()) {
			if ("(".equals(tokens.current()))
				braceCount++;
			else if (")".equals(tokens.current()))
				braceCount--;
			else if (variables.containsKey(tokens.current())) {
				Object val = variables.get(tokens.current());
				if (val instanceof Double)
					expval += val + " ";
				else if (val instanceof Brackets) {
					tokens.validateNext("[");
					String exp = parseStringExpandVars(variables, "]", tokens,
							ws);
					int index = Math.round((float) Expression
							.calculate(exp, ws).evaluate());
					if (index >= ((Brackets) val).size())
						throw new ParseException(
								"Index out of bounds! Array Size "
										+ ((Brackets) val).size()
										+ " index accessed " + index
										+ " at position " + tokens.position());
					//tokens.validateCurrent("]");
					expval += ((Brackets) val).get(index).evaluate() + " ";
				}
			} else 
				expval += tokens.current() + " ";
			
		}
		return expval;
	}

	static String parseUntilNextBrace(Stream tokens) throws ParseException {
		String expval = "";
		int braceCount = 0;
		while (!("}".equals(tokens.next()) && braceCount == 0)) {
			expval += tokens.current() + " ";
			if (tokens.peekNext() == null)
				throw new ParseException(
						"Poorly matched braces.\nAt: position "
								+ tokens.position());
			else if ("{".equals(tokens.current()))
				braceCount++;
			else if ("}".equals(tokens.current()))
				braceCount--;
		}
		return expval;
	}

	private static void parseWhile(HashMap<String, Object> variables,
			WorkSheet ws, Stream tokens) throws Exception {
		int lc = 0;
		tokens.validateNext("(");
		int pos = tokens.position();
		String condition = parseStringExpandVarsBrackets(variables, tokens, ws);
		tokens.validateNext("{");
		String code = parseUntilNextBrace(tokens);
		while (parseBooleanExp(ws, condition)) {
			lc++;
			Code.execute(variables, ws, code);
			tokens.setPosition(pos);
			condition = parseStringExpandVarsBrackets(variables, tokens, ws);
			tokens.validateNext("{");
			code = parseUntilNextBrace(tokens);
			if (lc > 10000)
				break;
		}
		tokens.validateCurrent("}");
	}

	private static void parseIf(HashMap<String, Object> variables,
			WorkSheet ws, Stream tokens) throws Exception {
		tokens.validateNext("(");
		String condition = parseStringExpandVarsBrackets(variables, tokens, ws);
		tokens.validateNext("{");
		String code = parseUntilNextBrace(tokens);
		if (parseBooleanExp(ws, condition)) {
			execute(variables, ws, code);
		}
		tokens.validateCurrent("}");
	}

	private static boolean parseBooleanExp(WorkSheet ws, String condition)
			throws Exception {
		int index = -1;
		int i = 0;
		for (; i < comparators.size(); i++) {
			if ((index = condition.indexOf(comparators.get(i))) != -1) {
				break;
			}
		}
		if (index == -1)
			return false;
		String comparator = comparators.get(i);
		String exp1 = condition.substring(0, index);
		String exp2 = condition.substring(index + comparator.length(),
				condition.length());
		double exp1val = Expression.calculate(exp1, ws).evaluate();
		double exp2val = Expression.calculate(exp2, ws).evaluate();
		if (comparator.equals("= ="))
			return equal(exp1val, exp2val);
		else if (comparator.equals("! ="))
			return notEqual(exp1val, exp2val);
		else if (comparator.equals("< ="))
			return leq(exp1val, exp2val);
		else if (comparator.equals("> ="))
			return geq(exp1val, exp2val);
		else if (comparator.equals("<"))
			return lt(exp1val, exp2val);
		else if (comparator.equals(">"))
			return gt(exp1val, exp2val);
		return false;
	}

	public static boolean equal(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.005;
	}

	public static boolean notEqual(double d1, double d2) {
		return !equal(d1, d2);
	}

	public static boolean gt(double d1, double d2) {
		return !equal(d1, d2) && d1 > d2;
	}

	public static boolean lt(double d1, double d2) {
		return !equal(d1, d2) && d1 < d2;
	}

	public static boolean leq(double d1, double d2) {
		return equal(d1, d2) || d1 < d2;
	}

	public static boolean geq(double d1, double d2) {
		return equal(d1, d2) || d1 > d2;
	}
}
