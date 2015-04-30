package expressions;

public class Number extends Expression implements Value {
	double val;
	@Override
	public String show() {
		// TODO Auto-generated method stub
		return val+"";
	}

	@Override
	public double evaluate() {
		// TODO Auto-generated method stub
		return val;
	}

}
