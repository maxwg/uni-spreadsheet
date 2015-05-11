package expressions;


public class Log extends Expression implements FunctionOp {
	Brackets exprs;
	public static String SYMBOL = "LOG";

	public Log() {
	}

	public Log(Brackets exprs) {
		this.exprs = exprs;
	}

	@Override
	public String show() {
		return "log" + exprs.show();
	}

	@Override
	public double evaluate() {
		return Math.log(exprs.evaluate(0))/Math.log(exprs.evaluate(1));
	}

	@Override
	public String getToken() {
		return SYMBOL;
	}

	@Override
	public int noParameters() {
		return 2;
	}

	@Override
	public String toLatex() {
		return "\\log_{"+exprs.get(1).toLatex()+"} \\left( "+exprs.get(0).toLatex()+"\\right)";
	}
}
