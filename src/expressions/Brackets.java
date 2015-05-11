package expressions;


public class Brackets extends Expression {
	Expression[] expressions;

	public Brackets() {
	}

	public Brackets(Expression[] expressions) {
		this.expressions = expressions;
	}

	@Override
	public String show() {
		String show = "(";
		for(Expression exp : expressions)
			show += exp.show();
			show += ", ";
		show = show.substring(0, show.length()-2);
		show += ")";
		return show;
	}
	
	public int size(){
		return expressions.length;
	}
	
	public Expression get(int expr){
		return expressions[expr];
	}
	
	@Override
	public double evaluate() {
		return expressions[0].evaluate();
	}
	
	public double evaluate(int expr) {
		return expressions[expr].evaluate();
	}
	@Override
	public String toLatex() {
		String show = "\\left(";
		for(Expression exp : expressions)
			show += exp.show();
			show += ", ";
		show = show.substring(0, show.length()-2);
		show += "\\right)";
		return show;
	}
}
