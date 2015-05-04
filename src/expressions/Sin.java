package expressions;

import java.util.ArrayList;
import java.util.List;

import data.CellIndex;

public class Sin extends Expression implements FunctionOp {
	Expression ex1;
	public static String SYMBOL = "SIN";

	public Sin() {
	}

	public Sin(Expression ex1) {
		this.ex1 = ex1;
	}

	@Override
	public String show() {
		return "sin(" + ex1.show() + ")";
	}

	@Override
	public double evaluate() {
		return Math.sin(ex1.evaluate());
	}

	@Override
	public String getToken() {
		return SYMBOL;
	}

	@Override
	public int noParameters() {
		return 1;
	}

	@Override
	public String toLatex() {
		return "\\sin\\left("+ex1.toLatex()+"\\right)";
	}

	@Override
	public List<CellIndex> getReferencedCells() {
		List<CellIndex> ref = new ArrayList<CellIndex>();
		for(CellIndex r : ex1.getReferencedCells())
			ref.add(r);
		return ref;
	}

}
