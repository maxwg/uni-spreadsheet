package expressions;

public class Log extends Expression implements FunctionOp {
	Expression ex1;
	Expression base;
	public static String SYMBOL = "LOG";

	public Log() {
	}

	public Log(Expression base, Expression ex1) {
		this.ex1 = ex1;
		this.base = base;
	}

	@Override
	public String show() {
		return "log(" + ex1.show() + "," + base.show()+ ")";
	}

	@Override
	public double evaluate() {
		return Math.log(ex1.evaluate())/Math.log(base.evaluate());
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
		return "\\log_{"+base.toLatex()+"} \\left( "+ex1.toLatex()+"\\right)";
	}

}
