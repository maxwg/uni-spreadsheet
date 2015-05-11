package expressions;


public class Ln extends Expression implements FunctionOp {
	Brackets exprs;
	public static String SYMBOL = "LN";

	public Ln() {
	}

	public Ln(Brackets exprs) {
		this.exprs = exprs;
	}

	@Override
	public String show() {
		return "ln" + exprs.show();
	}

	@Override
	public double evaluate() {
		return Math.log(exprs.evaluate());
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
		return "\\ln"+exprs.toLatex();
	}
}
