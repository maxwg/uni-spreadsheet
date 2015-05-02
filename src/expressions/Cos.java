package expressions;

public class Cos extends Expression implements FunctionOp {
	Expression ex1;
	public static String SYMBOL = "COS";

	public Cos() {
	}

	public Cos(Expression ex1) {
		this.ex1 = ex1;
	}

	@Override
	public String show() {
		return "cos(" + ex1.show() + ")";
	}

	@Override
	public double evaluate() {
		return Math.cos(ex1.evaluate());
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
		return "\\cos\\left("+ex1.toLatex()+"\\right)";
	}

}