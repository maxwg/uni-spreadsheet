package expressions;


public class Cos extends Expression implements FunctionOp {
	Brackets exprs;
	public static String SYMBOL = "COS";

	public Cos() {
	}

	public Cos(Brackets exprs) {
		this.exprs = exprs;
	}

	@Override
	public String show() {
		return "cos" + exprs.show();
	}

	@Override
	public double evaluate() {
		return Math.cos(exprs.evaluate());
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
		return "\\cos"+exprs.toLatex();
	}
}
