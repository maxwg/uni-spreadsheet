package expressions;

import java.util.ArrayList;
import java.util.List;

import data.CellIndex;

public class Ln extends Expression implements FunctionOp {
	Expression ex1;
	public static String SYMBOL = "LN";

	public Ln() {
	}

	public Ln(Expression ex1) {
		this.ex1 = ex1;
	}

	@Override
	public String show() {
		return "ln" + ex1.show();
	}

	@Override
	public double evaluate() {
		return Math.log(ex1.evaluate());
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
		// TODO Auto-generated method stub
		return "\\ln"+ex1.toLatex();
	}
}
