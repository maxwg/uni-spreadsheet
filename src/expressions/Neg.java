package expressions;


public class Neg extends Expression {
	Expression ex1;

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
	public String toLatex() {
		return "-"+ex1.toLatex();
	}
}
