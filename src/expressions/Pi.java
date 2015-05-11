package expressions;


public class Pi extends Expression implements Const {
	private static String TOKEN = "PI";
	public Pi(){}
	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return TOKEN;
	}

	@Override
	public String show() {
		// TODO Auto-generated method stub
		return TOKEN;
	}

	@Override
	public double evaluate() {
		// TODO Auto-generated method stub
		return Math.PI;
	}
	@Override
	public String toLatex() {
		return "\\pi";
	}
}
