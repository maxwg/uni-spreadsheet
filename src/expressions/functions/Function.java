package expressions.functions;

import helpers.Tokenizer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import data.WorkSheet;
import dataStructures.ListStream;
import expressions.Brackets;
import expressions.Expression;
import expressions.FunctionOp;
import expressions.ParseException;

/**
 * @author max
 * 
 *         Grammar: <functionName>(<[1..int.MaxValue | n ]> <paramName>){ <TYPE>
 *         <varName> (= value) ; <varname> = value; while(<var1> <BOOLEANOP>
 *         <var2>){ (infunction methods) } }
 */
public class Function extends Expression implements FunctionOp {
	Brackets params;
	String name;
	String code;
	String paramName;
	WorkSheet ws;
	double paramCount;

	public Function(Brackets params, String name, String code,
			String paramName, WorkSheet ws, double paramCount) {
		this.params = params;
		this.name = name;
		this.code = code;
		this.paramName = paramName;
		this.ws = ws;
		this.paramCount = paramCount;
	}

	public static ArrayList<Funclass> parseFunctions(String funcs, WorkSheet ws)
			throws IOException, ParseException {
		ArrayList<Object> toks = Tokenizer.tokenize(funcs);
		ListStream tokens = new ListStream(toks);
		ArrayList<Funclass> functions = new ArrayList<Funclass>();
		while (tokens.hasNext()) {
			if (tokens.current() instanceof Double)
				throw new ParseException("Expected Function Name but got "
						+ tokens.current() + " instead at position "
						+ tokens.position());
			String fname = (String) tokens.current();
			tokens.validateNext("(");
			tokens.next();
			double paramCount = tokens.current().equals("n") ? 0
					: (double) tokens.current();
			String paramName = (String) tokens.next();
			tokens.validateNext(")");
			tokens.next();
			String code = Code.parseUntilNextBrace(tokens);
			if (tokens.hasEnded())
				throw new ParseException(
						"Poorly Formed Function: Check brace matching.\nAt: positon "
								+ tokens.position());
			tokens.validateCurrent("}");
			functions.add(new Funclass(fname, paramName, code, paramCount, ws));
		}
		return functions;
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int noParameters() {
		// TODO Auto-generated method stub
		return (int) Math.round(paramCount);
	}

	@Override
	public String show() {
		// TODO Auto-generated method stub
		return name + params.show();
	}

	@Override
	public double evaluate() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		if (params.size() != paramCount && paramCount != 0)
			return 0;
		variables.put("#" + paramName, (double) params.size());
		for (int i = 0; i < params.size(); i++) {
			variables.put(paramName, params);
		}
		try {
			return Code.execute(variables, ws, code);
		} catch (Exception e) {
			//JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String toLatex() {
		// TODO Auto-generated method stub
		return name + params.toLatex();
	}
}
