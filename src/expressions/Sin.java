package expressions;

public class Sin extends Expression implements UnaryOp{
	Expression ex1;
	public static String SYMBOL = "SIN";
	
	public Sin(){}
	
	public Sin(Expression ex1){
		this.ex1 = ex1;
	}
	
	@Override
	public String show() {
		// TODO Auto-generated method stub
		return "sin("+ex1.show()+")";
	}

	@Override
	public double evaluate() {
		// TODO Auto-generated method stub
		return Math.sin(ex1.evaluate());
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return SYMBOL;
	}

}
