package expressions;


public class Sin extends Expression implements FunctionOp {
	Brackets exprs;
	public static String SYMBOL = "SIN";

	public Sin() {
	}

	public Sin(Brackets ex1) {
		this.exprs = ex1;
	}

	@Override
	public String show() {
		return "sin" + exprs.show();
	}

	@Override
	public double evaluate() {
		return Math.sin(exprs.evaluate());
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
		return "\\sin"+exprs.toLatex();
	}
}
