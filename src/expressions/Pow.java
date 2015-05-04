package expressions;

import java.util.ArrayList;
import java.util.List;

import data.CellIndex;

public class Pow extends Expression implements BinaryOp{
	Expression ex1;
	Expression ex2;
	public static String SYMBOL = "^";
	
	public Pow(){}
	
	public Pow(Expression ex1, Expression ex2){
		this.ex1 = ex1;
		this.ex2 = ex2;
	}
	
	@Override
	public String show() {
		// TODO Auto-generated method stub
		return ex1.show()+SYMBOL+ex2.show();
	}

	@Override
	public double evaluate() {
		// TODO Auto-generated method stub
		return Math.pow(ex1.evaluate(), ex2.evaluate());
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return SYMBOL;
	}

	@Override
	public String toLatex() {
		return "\\left("+ex1.toLatex()+"\\right)^{"+ex2.toLatex()+"}";
	}
}
