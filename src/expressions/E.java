package expressions;


public class E extends Expression implements Const {
	private static String TOKEN = "E";
	public E(){}
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
		return Math.E;
	}
	@Override
	public String toLatex() {
		return "e";
	}
}
