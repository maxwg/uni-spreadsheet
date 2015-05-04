package expressions;

import java.util.ArrayList;

import data.CellIndex;

public class Number extends Expression {
	double val;
	
	public Number(double val){
		this.val=val;
	}
	
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

	@Override
	public String toLatex() {
		if (val - Math.round(val) <0.005)
			return String.format( "%.0f", val);
		return  String.format( "%.2f", val);
	}
}
