package expressions;

public class Mult extends Expression implements BinaryOp{
	Expression ex1;
	Expression ex2;
	@Override
	public String show() {
		// TODO Auto-generated method stub
		return ex1.show()+"*"+ex2.show();
	}

	@Override
	public double evaluate() {
		// TODO Auto-generated method stub
		return ex1.evaluate() * ex2.evaluate();
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return "*";
	}

}
