package expressions;

import java.util.ArrayList;
import java.util.List;

import data.CellIndex;

public class Brackets extends Expression implements FunctionOp {
	Expression ex1;
	public static String SYMBOL = "";

	public Brackets() {
	}

	public Brackets(Expression ex1) {
		this.ex1 = ex1;
	}

	@Override
	public String show() {
		return "(" + ex1.show() + ")";
	}

	@Override
	public double evaluate() {
		return ex1.evaluate();
	}

	@Override
	public String getToken() {
		return SYMBOL;
	}

	@Override
	public int noParameters() {
		return 1;
	}

	@Override
	public String toLatex() {
		return "\\left("+ex1.toLatex()+"\\right)";
	}
}
