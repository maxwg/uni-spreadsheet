package expressions;


public class Neg extends Expression implements FunctionOp {
	Expression ex1;
	public static String SYMBOL = "-";

	public Neg() {
	}

	public Neg(Expression ex1) {
		this.ex1 = ex1;
	}

	@Override
	public String show() {
		return "-" + ex1.show();
	}

	@Override
	public double evaluate() {
		return -ex1.evaluate();
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
		return "-"+ex1.toLatex();
	}
}
